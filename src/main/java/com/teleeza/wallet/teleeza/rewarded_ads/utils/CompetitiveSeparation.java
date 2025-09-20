package com.teleeza.wallet.teleeza.rewarded_ads.utils;

import com.amazonaws.services.rekognition.model.Video;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.AudioAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.TextAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.VideoAds;
import com.teleeza.wallet.teleeza.rewarded_ads.entity.sponsored_reward_ads.SponsoredAds;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The pointer i keeps track of the end of the portion of list that has been traversed.
 * The pointer j scan the remaining unprocessed portion of the list
 */


public class CompetitiveSeparation {
    public static Object swapVideoAds(List<VideoAds> videoAds) {
        int i = 0;
        int j = 1;

       /* while (j < videoAds.size()) {
            VideoAds currentAd = videoAds.get(i);
            VideoAds nextAd = videoAds.get(j);

            if (!currentAd.getCompany().equals(nextAd.getCompany())) {
                if (currentAd.getIndustry().equals(nextAd.getIndustry())) {
                    // Swap the ads and insert a new random ad between them
                    VideoAds randomAd = getRandomVideoAd(videoAds);
                    videoAds.set(j, randomAd);
                }
            }

            i++;
            j++;
        }*/

        return videoAds;
//        int i = 0;
//        int j = 1;
//        while (j < videoAds.size()) {
//            if (j == 0 || !videoAds.get(j).getCompany().equals(videoAds.get(j - 1).getCompany()) &&
//                    !videoAds.get(j).getIndustry().equals(videoAds.get(j - 1).getIndustry())
//            ) {
//                if (i != j) {
//                    VideoAds ads = videoAds.get(i);
//                    videoAds.set(i, videoAds.get(j));
//                    videoAds.set(j, ads);
//                }
//                i++;
//            }
//            j++;
//        }
//        return videoAds;
    }

    private static VideoAds getRandomVideoAd(List<VideoAds> videoAds) {
        Random random = new Random();
        int randomIndex = random.nextInt(videoAds.size());
        return videoAds.get(randomIndex);
    }

    public static Object swapAudioAds(List<AudioAds> audioAds) {
//        int i = 0;
//        int j = 0;
//        while (j < audioAds.size()) {
//            if (j == 0 || audioAds.get(i).getCompany().equals(audioAds.get(j - 1).getCompany()) ||
//                    !audioAds.get(j).getIndustry().equals(audioAds.get(j - 1).getIndustry())) {
//                if (i != j) {
//                    AudioAds ads = audioAds.get(i);
//                    audioAds.set(i, audioAds.get(j));
//                    audioAds.set(j, ads);
//                }
//                i++;
//            }
//            j++;
//        }
//        return audioAds;
        /*int i = 0;
        int j = 1;

        while (j < audioAds.size()) {
            AudioAds currentAd = audioAds.get(i);
            AudioAds nextAd = audioAds.get(j);

            if (!currentAd.getCompany().equals(nextAd.getCompany())) {
                if (currentAd.getIndustry().equals(nextAd.getIndustry())) {
                    // Swap the ads and insert a new random ad between them
                    AudioAds randomAd = getRandomAudioAd(audioAds);
                    audioAds.set(j, randomAd);
                }
            }

            i++;
            j++;
        }*/

        return audioAds;
    }

    private static AudioAds getRandomAudioAd(List<AudioAds> audioAds) {
        Random random = new Random();
        int randomIndex = random.nextInt(audioAds.size());
        return audioAds.get(randomIndex);
    }

    public static List<VideoAds> filterVideoAds(List<VideoAds> videoAds, String userLocation, String userGender, String userAgeGroup) {
        Map<String, Object> response = new HashMap<>();

        List<VideoAds> locationFilteredVideoAds = videoAds.stream()
                .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                        .map(String::trim)
                        .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                .collect(Collectors.toList());

        locationFilteredVideoAds = locationFilteredVideoAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());
        locationFilteredVideoAds = locationFilteredVideoAds.stream()
                .filter(ad -> Arrays.stream(ad.getAge_group().split(","))
                        .map(String::trim)
                        .anyMatch(ageGroup -> ageGroup.equalsIgnoreCase(userAgeGroup)))
                .collect(Collectors.toList());


        response.put("videoAds", locationFilteredVideoAds);
        return locationFilteredVideoAds;
    }

    public static List<AudioAds> filterAudioAds(List<AudioAds> audioAds, String userLocation, String userGender, String userAgeGroup) {
        Map<String, Object> response = new HashMap<>();

        List<AudioAds> locationFilteredAudioAds = audioAds.stream()
                .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                        .map(String::trim)
                        .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                .collect(Collectors.toList());

        locationFilteredAudioAds = locationFilteredAudioAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());

        locationFilteredAudioAds = locationFilteredAudioAds.stream()
                .filter(ad -> Arrays.stream(ad.getAge_group().split(","))
                        .map(String::trim)
                        .anyMatch(ageGroup -> ageGroup.equalsIgnoreCase(userAgeGroup)))
                .collect(Collectors.toList());

        response.put("audioAds", locationFilteredAudioAds);
        return locationFilteredAudioAds;
    }

    public static List<TextAds> filterTextAds(List<TextAds> textAds, String userLocation, String userGender, String userAgeGroup) {
        Map<String, Object> response = new HashMap<>();

        List<TextAds> locationFilteredTextAds = textAds.stream()
                .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                        .map(String::trim)
                        .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                .collect(Collectors.toList());

        locationFilteredTextAds = locationFilteredTextAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());

        locationFilteredTextAds = locationFilteredTextAds.stream()
                .filter(ad -> Arrays.stream(ad.getAge_group().split(","))
                        .map(String::trim)
                        .anyMatch(ageGroup -> ageGroup.equalsIgnoreCase(userAgeGroup)))
                .collect(Collectors.toList());

        response.put("textAds", locationFilteredTextAds);
        return locationFilteredTextAds;
    }

    public static List<SponsoredAds> filterSponsoredAds(List<SponsoredAds> sponsoredAds, String userLocation, String userGender, String userAgeGroup) {
        Map<String, Object> response = new HashMap<>();

        List<SponsoredAds> locationFilteredSponsoredAds = sponsoredAds.stream()
                .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                        .map(String::trim)
                        .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                .collect(Collectors.toList());

        locationFilteredSponsoredAds = locationFilteredSponsoredAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());

        locationFilteredSponsoredAds = locationFilteredSponsoredAds.stream()
                .filter(ad -> Arrays.stream(ad.getAge_group().split(","))
                        .map(String::trim)
                        .anyMatch(ageGroup -> ageGroup.equalsIgnoreCase(userAgeGroup)))
                .collect(Collectors.toList());

        response.put("sponsoredAds", locationFilteredSponsoredAds);
        return locationFilteredSponsoredAds;
    }


    public static Object filterRewardAds(List<AudioAds> audioAds, List<VideoAds> videoAds, List<TextAds> textAds, String userLocation, String userGender) {

        Map<String, Object> response = new HashMap<>();

        List<VideoAds> locationFilteredVideoAds = videoAds.stream()
                .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                        .map(String::trim)
                        .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                .collect(Collectors.toList());

        List<AudioAds> locationFilteredAudioAds = audioAds.stream()
                .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                        .map(String::trim)
                        .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                .collect(Collectors.toList());

        List<TextAds> locationFilteredTextAds = textAds.stream()
                .filter(ad -> Arrays.stream(ad.getLocation().split(","))
                        .map(String::trim)
                        .anyMatch(location -> location.equalsIgnoreCase(userLocation)))
                .collect(Collectors.toList());

        locationFilteredVideoAds = locationFilteredVideoAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());

        locationFilteredAudioAds = locationFilteredAudioAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());

        locationFilteredTextAds = locationFilteredTextAds.stream()
                .filter(ad -> Arrays.stream(ad.getGender().split(","))
                        .map(String::trim)
                        .anyMatch(gender -> gender.equalsIgnoreCase(userGender)))
                .collect(Collectors.toList());

        response.put("audioAds", locationFilteredAudioAds);
        response.put("videoAds", locationFilteredVideoAds);
        response.put("textAds", locationFilteredTextAds);
        response.put("StatusCode", 200);
        return response;
    }


    public static Object swapTextAds(List<TextAds> textAds) {
        int i = 0;
        int j = 1;

        /*while (j < textAds.size()) {
            TextAds currentAd = textAds.get(i);
            TextAds nextAd = textAds.get(j);

            if (!currentAd.getCompany().equals(nextAd.getCompany())) {
                if (currentAd.getIndustry().equals(nextAd.getIndustry())) {
                    // Swap the ads and insert a new random ad between them
                    TextAds randomAd = getRandomTextAd(textAds);
                    textAds.set(j, randomAd);
                }
            }

            i++;
            j++;
        }*/

        return textAds;
//        int i = 0;
//        int j = 0;
//
//        while (j < textAds.size()) {
//            if (j == 0 || textAds.get(i).getCompany().equals(textAds.get(j - 1).getCompany()) ||
//                    !textAds.get(j).getIndustry().equals(textAds.get(j - 1).getIndustry())) {
//                if (i != j) {
//                    TextAds ads = textAds.get(i);
//                    textAds.set(i, textAds.get(j));
//                    textAds.set(j, ads);
//                }
//                i++;
//            }
//            j++;
//        }
//        return textAds;
    }

    private static TextAds getRandomTextAd(List<TextAds> textAds) {
        Random random = new Random();
        int randomIndex = random.nextInt(textAds.size());
        return textAds.get(randomIndex);
    }
}
