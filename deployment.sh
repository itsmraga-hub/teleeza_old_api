#!/bin/bash

#Custom Java Service Wrapper - not like a real daemon

#Declare re-usable constants
[[ -n "$DEBUG" ]] && set -x

#Turn on color prompt: for ubuntu servers
force_color_prompt=yes

# Initialize stop wait time if not provided by the config file
#[[ -z "$STOP_WAIT_TIME" ]] && STOP_WAIT_TIME="{{stopWaitTime:60}}"
STOP_WAIT_TIME=60

PID_FILE="pid/wallet.pid"
JAR_FILE="teleeza-2.0-wallet.jar"
LOG_FILE="logs/wallet.log"

# ANSI Colors
echoRed() { echo $'\e[091m'"$1"$'\e[0m'; }
echoGreen() { echo $'\e[092m'"$1"$'\e[0m'; }
echoYellow() { echo $'\e[093m'"$1"$'\e[0m'; }

# Utility functions
checkPermissions() {
  touch "$PID_FILE" &> /dev/null || { echoRed "Operation not permitted (cannot access pid file)"; return 4; }
}

#Check if the PID file exists
check_if_pid_file_exists() {
    if [ ! -f $PID_FILE ];
    then
        echoRed '[ERROR]----------------------------------------------------------------------------'
        echoRed "[ERROR] $PID_FILE file not found: $PID_FILE"
        echoRed '[ERROR]----------------------------------------------------------------------------'
    else
        echoGreen '[INFO]----------------------------------------------------------------------------'
        echoGreen "[INFO] $PID_FILE file found"
        echoGreen '[INFO]----------------------------------------------------------------------------'
    fi
}

await_file() {
  end=$(date +%s)
  let "end+=10"
  while [[ ! -s "$1" ]]
  do
    now=$(date +%s)
    if [[ $now -ge $end ]]; then
      break
    fi
    sleep 1
  done
}

isRunning() {
  ps -p "$1" &> /dev/null
}

do_start() {
    echoGreen '[INFO]----------------------------------------------------------------------------'
    echoGreen '[INFO] Starting the application for wallet'
    echoGreen '[INFO]----------------------------------------------------------------------------'

  # Redirects default and error output to a log file
  java -jar ${JAR_FILE} -Xmx256m >> ${LOG_FILE} 2>&1 &

#  Wait for the PID file
  await_file "$PID_FILE"
  pid=$(cat "$PID_FILE")

  [[ -z ${pid} ]] && {
        echoRed '[ERROR]----------------------------------------------------------------------------'
        echoRed '[ERROR] Application failed to start for wallet.'
        echoRed '[ERROR]----------------------------------------------------------------------------'
   return 1;
   }

    echoGreen '[INFO]----------------------------------------------------------------------------'
    echoGreen "[INFO] Application started with PID [$pid] "
    echoGreen '[INFO]----------------------------------------------------------------------------'
}

do_stop() {
  kill "$1" &> /dev/null || {
    echoRed '[ERROR]----------------------------------------------------------------------------'
    echoRed "[ERROR] Unable to kill process $1 "
    echoRed '[ERROR]----------------------------------------------------------------------------'
    return 1;
  }

  for i in $(seq 1 $STOP_WAIT_TIME); do
    isRunning "$1" || {
        echoGreen '[INFO]----------------------------------------------------------------------------'
        echoGreen "[INFO] Stopped [$1] successfully.  "
        echoGreen '[INFO]----------------------------------------------------------------------------'
        rm -f "$2"; return 0;
    }
    [[ $i -eq STOP_WAIT_TIME/2 ]] && kill "$1" &> /dev/null
    sleep 1
  done

  echoRed '[ERROR]----------------------------------------------------------------------------'
  echoRed "[ERROR] Unable to kill process $1 "
  echoRed '[ERROR]----------------------------------------------------------------------------'
  return 1;
}

#Check the application status
check_status(){
    check_if_pid_file_exists
    [[ -f "$PID_FILE" ]] || {
        echoRed '[ERROR]----------------------------------------------------------------------------'
        echoRed '[ERROR] Application is not running.'
        echoRed '[ERROR]----------------------------------------------------------------------------'
        return 3;
    }
    pid=$(cat "$PID_FILE")
    isRunning "$pid" || {
        echoRed '[INFO]----------------------------------------------------------------------------'
        echoRed "[INFO] Application is not running: process ${pid} not found"
        echoRed '[INFO]----------------------------------------------------------------------------'
        return 1;
    }
    echoGreen '[INFO]----------------------------------------------------------------------------'
    echoGreen "[INFO] Application is running with PID [$pid]"
    echoGreen '[INFO]----------------------------------------------------------------------------'

    # In any other case, return 0
    return 0
}

start(){
  if [[ -f "$PID_FILE" ]]; then
    pid=$(cat "$PID_FILE")
    isRunning "$pid" && {
        echoYellow '[WARN]----------------------------------------------------------------------------'
        echoYellow "[WARN] Application is running -  [$PID_FILE]"
        echoYellow '[WARN]----------------------------------------------------------------------------'
        return 0;
    }
  fi
  do_start "$@"
}

stop() {
   if [ ! -f $PID_FILE ];then {
        echoYellow '[WARN]----------------------------------------------------------------------------'
        echoYellow "[WARN] Application not running -  [$PID_FILE] not found"
        echoYellow '[WARN]----------------------------------------------------------------------------'
	    return 0;
	}
	fi

    pid=$(cat "$PID_FILE")
    isRunning "$pid" || {
        echoYellow '[WARN]----------------------------------------------------------------------------'
        echoYellow "[WARN] Application not running (process ${pid}). Removing stale PID_FILE file."
        echoYellow '[WARN]----------------------------------------------------------------------------'
        rm -f "PID_FILE";
        return 0;
     }
    do_stop "$pid" "$PID_FILE"
}

#Expose commands to handle the app
case "$1" in
status)
    check_status
    ;;
start)
    start "$@"; exit $?;;
stop)
    stop
    ;;

restart|reload)
    stop && start
    ;;
*)
echo "Usage: $0 {status|start|stop|restart}"
    exit 1
esac

exit 0
