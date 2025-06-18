package com.annular.filmhook.service;

import com.annular.filmhook.model.*;
import com.annular.filmhook.repository.*;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.webmodel.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerInfoRepository sellerInfoRepository;
    private final SellerMediaFileRepository sellerMediaFileRepository;
    private final UserRepository userRepository;
    private final S3Util s3Util;

    public SellerInfo saveSellerInfo(SellerInfoDTO dto, SellerFileInputModel files) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow();

        SellerInfo seller = SellerInfo.builder()
                .firstName(dto.getFirstName())
                .middleName(dto.getMiddleName())
                .lastName(dto.getLastName())
                .citizenship(dto.getCitizenship())
                .placeOfBirth(dto.getPlaceOfBirth())
                .idProofNumber(dto.getIdProofNumber())
                .doorFlatNumber(dto.getDoorFlatNumber())
                .streetCross(dto.getStreetCross())
                .area(dto.getArea())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .businessInfo(mapBusinessInfo(dto.getBusinessInfo()))
                .shopInfo(mapShopInfo(dto.getShopInfo()))
                .gstVerification(mapGstVerification(dto.getGstVerification()))
                .user(user)
                .build();

        seller = sellerInfoRepository.save(seller);

        if (files != null) {
            if (files.getIdProofImages() != null && !files.getIdProofImages().isEmpty()) {
                SellerMediaFile idProof = saveMediaFile(files.getIdProofImages().get(0), seller, "ID_PROOF");
                seller.setIdProofImage(idProof);
            }
            if (files.getShopLogos() != null && !files.getShopLogos().isEmpty()) {
                SellerMediaFile shopLogo = saveMediaFile(files.getShopLogos().get(0), seller, "SHOP_LOGO");
                seller.setShopLogo(shopLogo);
            }
        }

        return sellerInfoRepository.save(seller);
    }

    public SellerInfo updateSellerInfo(Long sellerId, SellerInfoDTO dto, SellerFileInputModel files) {
        SellerInfo existing = sellerInfoRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("SellerInfo not found for sellerId: " + sellerId));

        existing.setFirstName(dto.getFirstName());
        existing.setMiddleName(dto.getMiddleName());
        existing.setLastName(dto.getLastName());
        existing.setCitizenship(dto.getCitizenship());
        existing.setPlaceOfBirth(dto.getPlaceOfBirth());
        existing.setIdProofNumber(dto.getIdProofNumber());
        existing.setDoorFlatNumber(dto.getDoorFlatNumber());
        existing.setStreetCross(dto.getStreetCross());
        existing.setArea(dto.getArea());
        existing.setState(dto.getState());
        existing.setPostalCode(dto.getPostalCode());
        existing.setBusinessInfo(mapBusinessInfo(dto.getBusinessInfo()));
        existing.setShopInfo(mapShopInfo(dto.getShopInfo()));
        existing.setGstVerification(mapGstVerification(dto.getGstVerification()));

        // 3. Handle Shop Logo update (without deleting DB record)
        if (files.getShopLogos() != null && !files.getShopLogos().isEmpty()) {
            MultipartFile newLogoFile = files.getShopLogos().get(0);
            if (existing.getShopLogo() != null) {
                updateMediaFile(existing.getShopLogo(), newLogoFile, "SHOP_LOGO", sellerId);
            } else {
                SellerMediaFile shopLogo = saveMediaFile(newLogoFile, existing, "SHOP_LOGO");
                existing.setShopLogo(shopLogo);
            }
        }

        // 4. Handle ID Proof update
        if (files.getIdProofImages() != null && !files.getIdProofImages().isEmpty()) {
            MultipartFile newIdProofFile = files.getIdProofImages().get(0);
            if (existing.getIdProofImage() != null) {
                updateMediaFile(existing.getIdProofImage(), newIdProofFile, "ID_PROOF", sellerId);
            } else {
                SellerMediaFile idProof = saveMediaFile(newIdProofFile, existing, "ID_PROOF");
                existing.setIdProofImage(idProof);
            }
        }

        return sellerInfoRepository.save(existing);
    }

    private SellerMediaFile saveMediaFile(MultipartFile file, SellerInfo seller, String category) {
        String uploadedUrl = uploadFileToS3(file, category, seller.getId());

        return sellerMediaFileRepository.save(SellerMediaFile.builder()
                .seller(seller)
                .category(category)
                .fileId(UUID.randomUUID().toString())
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .filePath(uploadedUrl)
                .status(true)
                .createdBy(seller.getUser().getUserId())
                .updatedBy(seller.getUser().getUserId())
                .notificationCount(0)
                .unverifiedList(false)
                .build());
    }

    // NEW: Reuse and update existing media record
    private void updateMediaFile(SellerMediaFile existingMedia, MultipartFile file, String category, Long sellerId) {
        s3Util.deleteFileFromS3(existingMedia.getFilePath());
        String newUrl = uploadFileToS3(file, category, sellerId);

        existingMedia.setFileName(file.getOriginalFilename());
        existingMedia.setFileSize(file.getSize());
        existingMedia.setFileType(file.getContentType());
        existingMedia.setFilePath(newUrl);
        existingMedia.setCategory(category);
        existingMedia.setUpdatedBy(existingMedia.getSeller().getUser().getUserId());

        sellerMediaFileRepository.save(existingMedia);
    }

    private String uploadFileToS3(MultipartFile file, String folder, Long sellerId) {
        try {
            AwsCredentialsProvider credentialsProvider = s3Util.getAwsCredentialsProvider();
            Region region = Region.of(s3Util.getS3RegionName());

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String s3Key = folder + "/" + sellerId + "/" + fileName;

            try (S3Client s3Client = S3Client.builder()
                    .region(region)
                    .credentialsProvider(credentialsProvider)
                    .build()) {

                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(s3Util.getS3BucketName())
                        .key(s3Key)
                        .contentType(file.getContentType())
                        .build();

                s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
            }

            return s3Util.getS3BaseURL() + "/" + s3Util.getS3BucketName() + "/" + s3Key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    private BusinessInfo mapBusinessInfo(BusinessInfoDTO dto) {
        BusinessAddress address = new BusinessAddress();
        address.setLocation(dto.getLocation());
        address.setDoorFlatNumber(dto.getDoorFlatNumber());
        address.setStreetCross(dto.getStreetCross());
        address.setArea(dto.getArea());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());

        BusinessInfo info = new BusinessInfo();
        info.setBusinessName(dto.getBusinessName());
        info.setBusinessLocation(dto.getBusinessLocation());
        info.setBusinessType(dto.getBusinessType());
        info.setPanOrGstin(dto.getPanOrGstin());
        info.setBusinessAddress(address);

        return info;
    }

    private ShopInfo mapShopInfo(ShopInfoDTO dto) {
        ShopInfo info = new ShopInfo();
        info.setShopName(dto.getShopName());
        info.setPhoneNumber(dto.getPhoneNumber());
        info.setEmai(dto.getEmail());
        info.setProductSale(dto.isProductSale());
        return info;
    }

    private GstVerification mapGstVerification(GstVerificationDTO dto) {
        GstVerification gst = new GstVerification();
        gst.setGstNumber(dto.getGstNumber());
        gst.setVerifed(dto.isVerifed());
        return gst;
    }
    
    //Get seller by userId
    
    public SellerInfoDTO getSellerDetailsByUserId(Integer userId) {
        SellerInfo seller = sellerInfoRepository.findSellerInfoByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Seller not found for userId: " + userId));

        return SellerInfoDTO.builder()
                .firstName(seller.getFirstName())
                .middleName(seller.getMiddleName())
                .lastName(seller.getLastName())
                .citizenship(seller.getCitizenship())
                .placeOfBirth(seller.getPlaceOfBirth())
                .idProofNumber(seller.getIdProofNumber())
                .doorFlatNumber(seller.getDoorFlatNumber())
                .streetCross(seller.getStreetCross())
                .area(seller.getArea())
                .state(seller.getState())
                .postalCode(seller.getPostalCode())
                .idProofImage(SellerMediaFileDTO.from(seller.getIdProofImage()))
                .shopLogo(SellerMediaFileDTO.from(seller.getShopLogo()))
                .businessInfo(mapBusinessInfoToDto(seller.getBusinessInfo()))
                .shopInfo(mapShopInfoToDto(seller.getShopInfo()))
                .gstVerification(mapGstToDto(seller.getGstVerification()))
                .userId(seller.getUser() != null ? seller.getUser().getUserId() : null)
                .build();
    }
    
    private BusinessInfoDTO mapBusinessInfoToDto(BusinessInfo info) {
        if (info == null) return null;

        BusinessInfoDTO dto = new BusinessInfoDTO();
        dto.setBusinessLocation(info.getBusinessLocation());
        dto.setBusinessType(info.getBusinessType());
        dto.setBusinessName(info.getBusinessName());
        dto.setPanOrGstin(info.getPanOrGstin());

        if (info.getBusinessAddress() != null) {
            dto.setLocation(info.getBusinessAddress().getLocation());
            dto.setDoorFlatNumber(info.getBusinessAddress().getDoorFlatNumber());
            dto.setStreetCross(info.getBusinessAddress().getStreetCross());
            dto.setArea(info.getBusinessAddress().getArea());
            dto.setState(info.getBusinessAddress().getState());
            dto.setPostalCode(info.getBusinessAddress().getPostalCode());
        }

        return dto;
    }

    private ShopInfoDTO mapShopInfoToDto(ShopInfo shopInfo) {
        if (shopInfo == null) return null;

        ShopInfoDTO dto = new ShopInfoDTO();
        dto.setShopName(shopInfo.getShopName());
        dto.setPhoneNumber(shopInfo.getPhoneNumber());
        dto.setEmail(shopInfo.getEmai());
        dto.setProductSale(shopInfo.isProductSale());
        return dto;
    }

    private GstVerificationDTO mapGstToDto(GstVerification gst) {
        if (gst == null) return null;

        GstVerificationDTO dto = new GstVerificationDTO();
        dto.setGstNumber(gst.getGstNumber());
        dto.setVerifed(gst.isVerifed());
        return dto;
    }
    
    //deleteSeller
    
    public void deleteSellerById(Long sellerId) {
        SellerInfo seller = sellerInfoRepository.findById(sellerId)
            .orElseThrow(() -> new RuntimeException("Seller not found with ID: " + sellerId));
        sellerInfoRepository.delete(seller);
    }

}
