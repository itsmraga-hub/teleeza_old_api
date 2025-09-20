package com.teleeza.wallet.teleeza.customer_registration.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teleeza.wallet.teleeza.authentication.teleeza.entity.User;
import com.teleeza.wallet.teleeza.authentication.teleeza.repository.AuthRepository;
import com.teleeza.wallet.teleeza.common.config.TeleezaConfig;
import com.teleeza.wallet.teleeza.customer_registration.entities.CustomerEntity;
import com.teleeza.wallet.teleeza.customer_registration.payloads.dtos.response.KycUploadResponse;
import com.teleeza.wallet.teleeza.customer_registration.repository.CustomerRegistrationRepository;
import com.teleeza.wallet.teleeza.sasapay.service.SasaPayApi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.teleeza.wallet.teleeza.utils.Constants.AUTHORIZATION_HEADER_STRING;
import static com.teleeza.wallet.teleeza.utils.Constants.BEARER_AUTH_STRING;

@Service
@Slf4j
@Transactional
public class KycStorageService {
    @Value("${application.bucket.name}")
    private String bucketName;
    @Autowired
    private AmazonS3 s3Client;

    @Value("${application.bucket.s3url}")
    private String s3BaseUrl;
    @Autowired
    private SasaPayApi sasaPayApi;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    @Value("${image.folder}")
    private String imageFolder;
    private Integer imageSize = 100;
    private final CustomerRegistrationRepository repository;
    @Autowired
    private TeleezaConfig teleezaConfig;
    @Autowired
    private AuthRepository authRepository;

    private static final Logger logger = LoggerFactory.getLogger(KycStorageService.class);

    public KycStorageService(SasaPayApi sasaPayApi,
                             OkHttpClient okHttpClient,
                             ObjectMapper objectMapper,
                             CustomerRegistrationRepository repository) {
        this.sasaPayApi = sasaPayApi;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.repository = repository;
    }


    public String uploadMerchantProfileImage(MultipartFile file, String phoneNumber) throws IOException {
        ObjectMetadata data = new ObjectMetadata();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            if (s3Client.doesBucketExistV2(bucketName)) {
                data.setContentType(file.getContentType());
                data.setContentLength(file.getSize());
                s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), data)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withBucketKeyEnabled(true));
            }

        } catch (AmazonClientException ace) {
            logger.error("The client encountered an internal server error while performing this request:");
            logger.error("==================================================================");
            logger.error("Error Message: " + ace.getMessage());
            logger.error("==================================================================");
        } catch (IOException e) {
            logger.error("IOException Occurred");
            logger.error("================================");
            logger.error(e.getMessage());
            throw new RuntimeException(e);

        }

        User user = authRepository.findByPhone(phoneNumber).orElseThrow(
                ()-> new UsernameNotFoundException("User not found")
        );

        if (!org.springframework.util.StringUtils.isEmpty(fileName)) {
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host(s3BaseUrl)
                    .path("/" + bucketName + "/" + fileName)
                    .build()
                    .encode();

            user.setMerchantProfileImg(uriComponents.toString());

            authRepository.save(user);
            return uriComponents.toString();
        } else return "";
    }


    public String uploadUseMugshot(MultipartFile file, String accountNumber) throws IOException {
        ObjectMetadata data = new ObjectMetadata();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            if (s3Client.doesBucketExistV2(bucketName)) {
                data.setContentType(file.getContentType());
                data.setContentLength(file.getSize());
//                                s3Client.putObject(bucketName,"",multipartFile.getInputStream(),data);
                s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), data)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withBucketKeyEnabled(true));
//                        .withCannedAcl(CannedAccessControlList.PublicRead));
            }

        } catch (AmazonClientException ace) {
            logger.error("The client encountered an internal server error while performing this request:");
            logger.error("==================================================================");
            logger.error("Error Message: " + ace.getMessage());
            logger.error("==================================================================");
        } catch (IOException e) {
            logger.error("IOException Occurred");
            logger.error("================================");
            logger.error(e.getMessage());
            throw new RuntimeException(e);

        }
//        File fileObj = convertMultipartFileToFile(file);
//        String fileName = FilenameUtils.getBaseName(fileObj.getName())
//                + "."
//                + FilenameUtils.getExtension(fileObj.getName());

//        //read file from `images` folder and  resize image
//        BufferedImage bufferedImage = ImageIO.read(new File(imageFolder + "/" + fileObj));
//        BufferedImage outputImage = Scalr.resize(bufferedImage, 400);
//        String newFileName = FilenameUtils.getBaseName(fileObj.getName())
//                + "_" + imageSize.toString() + "."
//                + FilenameUtils.getExtension(fileObj.getName());
//
//        Path path = Paths.get(imageFolder, newFileName);
//        File newImageFile = path.toFile();
//        ImageIO.write(outputImage, "jpg", newImageFile);
//        outputImage.flush();
//        logger.info("======Resized Image=====");
//        logger.info("Output Image  :   >> {}", outputImage);
//        logger.info("New File Name  :   >> {}", newFileName);
//        logger.info("New Image File   :   >> {}", newImageFile.getName());


        CustomerEntity customer = repository.findByCustomerAccountNumber(accountNumber);

        if (!org.springframework.util.StringUtils.isEmpty(fileName)) {
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host(s3BaseUrl)
                    .path("/" + bucketName + "/" + fileName)
                    .build()
                    .encode();

            customer.setPhotoUrl(uriComponents.toString());

            repository.save(customer);
            return uriComponents.toString();
        } else return "";
    }

    public String uploadFileToS3Directly(MultipartFile multipartFile) {
        ObjectMetadata data = new ObjectMetadata();
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        try {
            if (s3Client.doesBucketExistV2(bucketName)) {
                data.setContentType(multipartFile.getContentType());
                data.setContentLength(multipartFile.getSize());
//                                s3Client.putObject(bucketName,"",multipartFile.getInputStream(),data);
                s3Client.putObject(new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), data)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withBucketKeyEnabled(true));
//                        .withCannedAcl(CannedAccessControlList.PublicRead));
            }

        } catch (AmazonClientException ace) {
            logger.error("The client encountered an internal server error while performing this request:");
            logger.error("==================================================================");
            logger.error("Error Message: " + ace.getMessage());
            logger.error("==================================================================");
        } catch (IOException e) {
            logger.error("IOException Occurred");
            logger.error("================================");
            logger.error(e.getMessage());
            throw new RuntimeException(e);

        }

        //Generate file url
        if (!org.springframework.util.StringUtils.isEmpty(fileName)) {
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host(s3BaseUrl)
                    .path("/" + bucketName + "/" + fileName)
                    .build()
                    .encode();
            return uriComponents.toString();
//        try {
//
//            if (s3Client.doesBucketExistV2(bucketName)) {
//                data.setContentType(multipartFile.getContentType());
//                data.setContentLength(multipartFile.getSize());
////                s3Client.putObject(bucketName,"keyName",multipartFile.getInputStream(),data).getContentMd5();
//                s3Client.putObject(new PutObjectRequest(bucketName,multipartFile.getOriginalFilename(),multipartFile.getBytes().toString())
//                        .withCannedAcl(CannedAccessControlList.PublicRead));
//
//
//                if(!org.springframework.util.StringUtils.isEmpty(multipartFile.getOriginalFilename())){
//                    UriComponents uriComponents = UriComponentsBuilder.newInstance()
//                            .scheme("http")
//                            .host(s3BaseUrl)
//                            .path("/"+bucketName+"/"+multipartFile.getOriginalFilename())
//                            .build()
//                            .encode();
//
//                    return uriComponents.toString();
//                }else {
//                    return "File not uploaded";
//                }
////                s3Client.putObject(new PutObjectRequest(bucketName,fileName,fileObj)
////                        .withCannedAcl(CannedAccessControlList.PublicRead));
////                s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj)
////                        .withCannedAcl( CannedAccessControlList.PublicRead )
////                );
////                s3Client.putObject(new PutObjectRequest(bucketName,fileName2,fileObj2)
////                        .withCannedAcl(CannedAccessControlList.PublicRead));
//            }
//
//
//        }catch (Exception ex){
//            return ex.getMessage();
//        }

        } else {
            return "";
        }
    }


    public String uploadIdFront(MultipartFile file, String accountNumber) throws IOException {
        ObjectMetadata data = new ObjectMetadata();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            if (s3Client.doesBucketExistV2(bucketName)) {
                data.setContentType(file.getContentType());
                data.setContentLength(file.getSize());
//                                s3Client.putObject(bucketName,"",multipartFile.getInputStream(),data);
                s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), data)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withBucketKeyEnabled(true));
//                        .withCannedAcl(CannedAccessControlList.PublicRead));
            }

        } catch (AmazonClientException ace) {
            logger.error("The client encountered an internal server error while performing this request:");
            logger.error("==================================================================");
            logger.error("Error Message: " + ace.getMessage());
            logger.error("==================================================================");
        } catch (IOException e) {
            logger.error("IOException Occurred");
            logger.error("================================");
            logger.error(e.getMessage());
            throw new RuntimeException(e);

        }
//        File fileObj = convertMultipartFileToFile(file);
//        String fileName = FilenameUtils.getBaseName(fileObj.getName())
//                + "."
//                + FilenameUtils.getExtension(fileObj.getName());
        // resize image
//        BufferedImage bufferedImage = ImageIO.read(new File(imageFolder + "/" + fileObj));
//        BufferedImage outputImage = Scalr.resize(bufferedImage, 400);
//        String newFileName = FilenameUtils.getBaseName(fileObj.getName())
//                + "_" + imageSize.toString() + "."
//                + FilenameUtils.getExtension(fileObj.getName());
//
//        Path path = Paths.get(imageFolder, newFileName);
//        File newImageFile = path.toFile();
//        ImageIO.write(outputImage, "jpg", newImageFile);
//        outputImage.flush();
//        logger.info("======Resized Image=====");
//        logger.info("Output Image  :   >> {}", outputImage);
//        logger.info("New File Name  :   >> {}", newFileName);
//        logger.info("New Image File   :   >> {}", newImageFile.getName());


        CustomerEntity customer = repository.findByCustomerAccountNumber(accountNumber);

        //Generate file url
        if (!org.springframework.util.StringUtils.isEmpty(fileName)) {
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host(s3BaseUrl)
                    .path("/" + bucketName + "/" + fileName)
                    .build()
                    .encode();

            customer.setIdFront(uriComponents.toString());
            repository.save(customer);
            return uriComponents.toString();
        } else return "";
    }

    public String uploadIdBAck(MultipartFile file, String accountNumber) throws IOException {
        ObjectMetadata data = new ObjectMetadata();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            if (s3Client.doesBucketExistV2(bucketName)) {
                data.setContentType(file.getContentType());
                data.setContentLength(file.getSize());
//                                s3Client.putObject(bucketName,"",multipartFile.getInputStream(),data);
                s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), data)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withBucketKeyEnabled(true));
//                        .withCannedAcl(CannedAccessControlList.PublicRead));
            }

        } catch (AmazonClientException ace) {
            logger.error("The client encountered an internal server error while performing this request:");
            logger.error("==================================================================");
            logger.error("Error Message: " + ace.getMessage());
            logger.error("==================================================================");
        } catch (IOException e) {
            logger.error("IOException Occurred");
            logger.error("================================");
            logger.error(e.getMessage());
            throw new RuntimeException(e);

        }
//        File fileObj = convertMultipartFileToFile(file);
//        String fileName = FilenameUtils.getBaseName(fileObj.getName())
//                + "."
//                + FilenameUtils.getExtension(fileObj.getName());

        // resize image
//        BufferedImage bufferedImage = ImageIO.read(new File(imageFolder + "/" + fileObj));
//        BufferedImage outputImage = Scalr.resize(bufferedImage, 400, 400);
//        String newFileName = FilenameUtils.getBaseName(fileObj.getName())
//                + "_" + imageSize.toString() + "."
//                + FilenameUtils.getExtension(fileObj.getName());
//
//        Path path = Paths.get(imageFolder, newFileName);
//        File newImageFile = path.toFile();
//        ImageIO.write(outputImage, "jpg", newImageFile);
//        outputImage.flush();

        CustomerEntity customer = repository.findByCustomerAccountNumber(accountNumber);

        //Generate file url
        if (!org.springframework.util.StringUtils.isEmpty(fileName)) {
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host(s3BaseUrl)
                    .path("/" + bucketName + "/" + fileName)
                    .build()
                    .encode();

            customer.setIdBack(uriComponents.toString());
            customer.setIsKycSubmitted(true);
            repository.save(customer);
            return uriComponents.toString();
        } else return "";
    }

    // Submit E-KYC to SasaPay
    public KycUploadResponse uploadKyc(
            String beneficiaryAccNumber,
            MultipartFile passport,
            MultipartFile idFront,
            MultipartFile idBack
    ) throws IOException {
        File passPortObj = convertMultipartFileToFile(passport);
        File idFrontObj = convertMultipartFileToFile(idFront);
        File idBackObj = convertMultipartFileToFile(idBack);

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("MerchantCode", "669994")
                .addFormDataPart("BeneficiaryAccountNumber", beneficiaryAccNumber)
                .addFormDataPart("PassportSizePhoto", passPortObj.getName(),
                        RequestBody.create(okhttp3.MediaType.parse("application/octet-stream"),
                                new File(imageFolder + "/" + passPortObj.getAbsoluteFile().getName())))
                .addFormDataPart("DocumentImageFront", idFrontObj.getName(),
                        RequestBody.create(okhttp3.MediaType.parse("application/octet-stream"),
                                new File(imageFolder + "/" + idFrontObj.getAbsoluteFile().getName())))
                .addFormDataPart("DocumentImageBack", idBackObj.getName(),
                        RequestBody.create(okhttp3.MediaType.parse("application/octet-stream"),
                                new File(imageFolder + "/" + idBackObj.getAbsoluteFile().getName())))
                .build();

        Request request = new Request.Builder()
                .url("https://api.sasapay.app/api/v1/waas/customer-registration/kyc-upload/")
                .post(requestBody)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s",
                        BEARER_AUTH_STRING, sasaPayApi.getAccessToken().getAccessToken()))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            //Deserialize to Java object;
            return objectMapper.readValue(response.body().string(), KycUploadResponse.class);
        } catch (IOException ex) {
            return null;
        }
    }

    // Convert Multipart File to File
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {

        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(new File(imageFolder + "/" + convertedFile))) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipart to file", e);
        }
        return convertedFile;
    }
}
