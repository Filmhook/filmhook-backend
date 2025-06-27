package com.annular.filmhook.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.FilmProfessionPermanentDetail;
import com.annular.filmhook.model.FollowersRequest;
import com.annular.filmhook.model.Likes;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.ReportPost;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserProfilePin;

import com.annular.filmhook.repository.FilmProfessionPermanentDetailRepository;
import com.annular.filmhook.repository.FriendRequestRepository;
import com.annular.filmhook.repository.LikeRepository;
import com.annular.filmhook.repository.PinProfileRepository;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.ReportRepository;
import com.annular.filmhook.repository.UserRepository;

import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.ReportService;
import com.annular.filmhook.service.UserService;

import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.ReportPostWebModel;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    UserDetails userDetails;
    
	@Autowired
	private JavaMailSender javaMailSender;

    @Autowired
    UserService userService;

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    PostsRepository postsRepository;
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    FilmProfessionPermanentDetailRepository filmProfessionPermanentDetailRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PinProfileRepository pinProfileRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    FriendRequestRepository friendRequestRepository;

    @Autowired
    ReportRepository reportRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Override
    public ResponseEntity<?> addPostReport(ReportPostWebModel reportPostWebModel) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            ReportPost reportPost = new ReportPost();
            reportPost.setUserId(userDetails.userInfo().getId());
            reportPost.setPostId(reportPostWebModel.getPostId());
            reportPost.setReason(reportPostWebModel.getReason());
            reportPost.setStatus(false); // Assuming initially report status is false
            reportPost.setCreatedBy(userDetails.userInfo().getId()); // Assuming user who reports is the creator
            reportRepository.save(reportPost);

            // Step 1: Get Post details
            Optional<Posts> optionalPost = postsRepository.findById(reportPostWebModel.getPostId());
            if (optionalPost.isPresent()) {
                Posts post = optionalPost.get();

            Integer postOwnerId = post.getUser().getUserId();
            Optional<User> optionalUser = userRepository.findById(postOwnerId);

            if (optionalUser.isPresent()) {
                User postOwner = optionalUser.get();
                String userEmail = postOwner.getEmail();
                String subject = "Important: Your Post Has Been Reported on Film-Hook";

                // Step 3: Email Content
                StringBuilder content = new StringBuilder();
                content.append("<html><body>")
                        .append("<div style='text-align: center;'>")
                        .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png' width='200' alt='FilmHook Logo'>")
                        .append("</div>")
                        .append("<p>Dear ").append(postOwner.getName()).append(",</p>")
                        .append("<p>We have received a report against your post on <strong>Film-Hook Apps</strong>.</p>")
                        .append("<p><strong>Reported Reason:</strong> ").append(reportPostWebModel.getReason()).append("</p>")
                        .append("<p>Please note that if your post is found to be violating our community guidelines, it will be <strong>automatically deleted within 24 hours</strong>.</p>")
                        .append("<p>If you believe this was a mistake, please contact our support team immediately.</p>")
                        .append("<br><p>Best Regards,</p>")
                        .append("<p><b>Film-Hook Apps Team</b></p>")
                        .append("<p>📧 <a href='mailto:support@film-hookapps.com'>support@film-hookapps.com</a> | 🌐 <a href='https://film-hookapps.com/'>Visit Website</a></p>")
                        .append("<p>📲 Get the App:</p>")
                        .append("<p><a href='https://play.google.com/store/apps/details?id=com.projectfh&hl=en'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/PlayStore.jpeg' alt='Android' width='30'></a> ")
                        .append("<a href='#'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Apple.jpeg' alt='iOS' width='30'></a></p>")
                        .append("<p>📢 Follow Us:</p>")
                        .append("<p>")
                        .append("<a href='https://www.facebook.com/share/1BaDaYr3X6/?mibextid=qi2Omg' target='_blank'>")
                        .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/faceBook.jpeg' width='30'></a> ")
                        .append("<a href='https://x.com/Filmhook_Apps?t=KQJkjwuvBzTPOaL4FzDtIA&s=08/' target='_blank'>")
                        .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Twitter.jpeg' width='30'></a> ")
                        .append("<a href='https://www.threads.net/@filmhookapps/' target='_blank'>")
                        .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Threads.jpeg' width='30'></a> ")
                        .append("<a href='https://www.instagram.com/filmhookapps?igsh=dXdvNnB0ZGg5b2tx' target='_blank'>")
                        .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Instagram.jpeg' width='30'></a> ")
                        .append("<a href='https://youtube.com/@film-hookapps?si=oSz-bY4yt69TcThP' target='_blank'>")
                        .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Youtube.jpeg' width='30'></a>")
                        .append("<a href='https://www.linkedin.com/in/film-hook-68666a353' target='_blank'>")
                        .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/linedIn.jpeg' width='30'></a>")
                        .append("</p>")
                        .append("</body></html>");
                // Step 4: Send Email
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(userEmail);
                helper.setSubject(subject);
                helper.setText(content.toString(), true);
                javaMailSender.send(message);
            }
        }
            response.put("reportInfo", reportPost);
            logger.info("addMethod method end");
            return ResponseEntity.ok(new Response(1, "Add ReportPost successfully", response));
        } catch (Exception e) {
            logger.error("Error setting reportPost {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new Response(-1, "Error setting reportPost", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> getAllPostReport(ReportPostWebModel reportPostWebModel) {
        try {
            Map<String, Object> response = new HashMap<>();

            Pageable paging = PageRequest.of(reportPostWebModel.getPageNo() - 1, reportPostWebModel.getPageSize());
            Page<ReportPost> reportPosts = reportRepository.findAll(paging);
            List<Map<String, Object>> combinedDetailsList = new ArrayList<>();

            Map<String, Object> pageDetails = new HashMap<>();
            pageDetails.put("totalPages", reportPosts.getTotalPages());
            pageDetails.put("totalRecords", reportPosts.getTotalElements());

            for (ReportPost reportPost : reportPosts) {
                Posts post = postsRepository.findById(reportPost.getPostId()).orElse(null);
                if (post == null) continue;

                List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Post, post.getId());

                Set<String> professionNames = filmProfessionPermanentDetailRepository.getProfessionDataByUserId(post.getUser().getUserId()).stream()
                        .map(FilmProfessionPermanentDetail::getProfessionName)
                        .collect(Collectors.toSet());

                List<FollowersRequest> followersList = friendRequestRepository.findByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);

                Integer userId = userDetails.userInfo().getId();
                boolean likeStatus = likeRepository.findByPostIdAndUserId(post.getId(), userId)
                        .map(Likes::getStatus)
                        .orElse(false);

                boolean pinStatus = pinProfileRepository.findByPinProfileIdAndUserId(post.getUser().getUserId(), userId)
                        .map(UserProfilePin::isStatus)
                        .orElse(false);

                PostWebModel postWebModel = PostWebModel.builder()
                        .id(post.getId())
                        .userId(post.getUser().getUserId())
                        .userName(post.getUser().getName())
                        .postId(post.getPostId())
                        .userProfilePic(userService.getProfilePicUrl(post.getUser().getUserId()))
                        .description(post.getDescription())
                        .pinStatus(pinStatus)
                        .likeCount(post.getLikesCount())
                        .shareCount(post.getSharesCount())
                        .commentCount(post.getCommentsCount())
                        .promoteFlag(post.getPromoteFlag())
                        .postFiles(postFiles).likeStatus(likeStatus)
                        .privateOrPublic(post.getPrivateOrPublic())
                        .locationName(post.getLocationName())
                        .professionNames(professionNames)
                        .followersCount(followersList.size())
                        .build();

                Map<String, Object> combinedDetails = new HashMap<>();
                combinedDetails.put("postWebModel", postWebModel);

                // Fetch ReportTable details
                Optional<ReportPost> reportTableOptional = reportRepository.findById(reportPost.getReportPostId());
                if (reportTableOptional.isPresent()) {
                    ReportPost reportTable = reportTableOptional.get();
                    combinedDetails.put("reportDetails", reportTable);
                }
                combinedDetailsList.add(combinedDetails);
            }
            response.put("pageDetails", pageDetails);
            response.put("combinedDetailsList", combinedDetailsList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in getAllPostReport: {}", e.getMessage(), e);
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error retrieving post reports", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> getByPostReportId(ReportPostWebModel reportPostWebModel) {
        try {
            Optional<ReportPost> optionalReportPost = reportRepository.findById(reportPostWebModel.getReportPostId());
            if (optionalReportPost.isPresent()) {
                ReportPost reportPost = optionalReportPost.get();
                return ResponseEntity.ok(reportPost);
            } else {
                return ResponseEntity.ok().body(new Response(-1, "Report with ID " + reportPostWebModel.getReportPostId() + " not found", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error retrieving post reports", e.getMessage()));
        }

    }

    @Override
    public ResponseEntity<?> getAllReportsByPostId(ReportPostWebModel reportPostWebModel) {
        Map<String, Object> response = new HashMap<>();
        try {
            Pageable paging = PageRequest.of(reportPostWebModel.getPageNo() - 1, reportPostWebModel.getPageSize());
            Page<ReportPost> reportPosts = reportRepository.findAll(paging);

            List<Map<String, Object>> combinedDetailsList = new ArrayList<>();

            Map<String, Object> pageDetails = new HashMap<>();
            pageDetails.put("totalPages", reportPosts.getTotalPages());
            pageDetails.put("totalRecords", reportPosts.getTotalElements());

            Set<Integer> processedPostIds = new HashSet<>();

            for (ReportPost reportPost : reportPosts) {
                if (processedPostIds.contains(reportPost.getPostId())) continue;

                processedPostIds.add(reportPost.getPostId());

                Posts post = postsRepository.findById(reportPost.getPostId()).orElse(null);
                if (post == null) continue;

                List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Post, post.getId());

                Set<String> professionNames = filmProfessionPermanentDetailRepository.getProfessionDataByUserId(post.getUser().getUserId()).stream()
                        .map(FilmProfessionPermanentDetail::getProfessionName)
                        .collect(Collectors.toSet());

                List<FollowersRequest> followersList = friendRequestRepository.findByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);

                Integer userId = userDetails.userInfo().getId();
                boolean likeStatus = likeRepository.findByPostIdAndUserId(post.getId(), userId)
                        .map(Likes::getStatus)
                        .orElse(false);

                boolean pinStatus = pinProfileRepository.findByPinProfileIdAndUserId(post.getUser().getUserId(), userId)
                        .map(UserProfilePin::isStatus)
                        .orElse(false);

                PostWebModel postWebModel = PostWebModel.builder()
                        .id(post.getId())
                        .userId(post.getUser().getUserId())
                        .userName(post.getUser().getName())
                        .postId(post.getPostId())
                        .userProfilePic(userService.getProfilePicUrl(post.getUser().getUserId()))
                        .description(post.getDescription())
                        .pinStatus(pinStatus)
                        .likeCount(post.getLikesCount())
                        .shareCount(post.getSharesCount())
                        .commentCount(post.getCommentsCount())
                        .promoteFlag(post.getPromoteFlag())
                        .postFiles(postFiles)
                        .likeStatus(likeStatus)
                        .privateOrPublic(post.getPrivateOrPublic())
                        .locationName(post.getLocationName())
                        .professionNames(professionNames)
                        .followersCount(followersList.size())
                        .build();

                Map<String, Object> combinedDetails = new HashMap<>();
                combinedDetails.put("postWebModel", postWebModel);

//				// Fetch ReportTable details and collect user IDs who reported this post
//				List<ReportPost> reportDetailsList = reportRepository.findByPostId(reportPost.getPostId());
//				List<Integer> reportUserIds = reportDetailsList.stream()
//						.map(ReportPost::getUserId)
//						.collect(Collectors.toList());
//				long uniqueReportUserIdCount = reportDetailsList.stream().map(ReportPost::getUserId).distinct().count();
//
//				combinedDetails.put("reportDetails", reportDetailsList);
//				combinedDetails.put("reportUserIds", reportUserIds);
//				combinedDetails.put("reportUserIdCount", uniqueReportUserIdCount);
                // Fetch ReportTable details and collect user IDs who reported this post
                List<ReportPost> reportDetailsList = reportRepository.findByPostId(reportPost.getPostId());
                List<Integer> reportUserIdss = reportDetailsList.stream()
                        .map(ReportPost::getUserId)
                        .collect(Collectors.toList());

                // Fetch user details for the reportUserIds
                List<User> users = userRepository.findAllById(reportUserIdss);
                // Count the number of reports per user
                List<ReportPost> postData = reportRepository.findAll();
                Map<Integer, Long> reportCountsPerUser = postData.stream()
                        .collect(Collectors.groupingBy(ReportPost::getUserId, Collectors.counting()));

                // Create a list of maps, each containing a userId and userName
                List<Map<String, Object>> reportUserIds = users.stream()
                        .map(user -> {
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("userId", user.getUserId());
                            userMap.put("username", user.getName());
                            userMap.put("reportCount", reportCountsPerUser.getOrDefault(user.getUserId(), 0L));
                            return userMap;
                        })
                        .collect(Collectors.toList());

                // Count unique report user IDs
                long reportUserIdCount = reportDetailsList.stream()
                        .map(ReportPost::getUserId)
                        .distinct()
                        .count();

                // Add the details to combinedDetails
                combinedDetails.put("reportDetails", reportDetailsList);
                combinedDetails.put("reportUserIds", reportUserIds);
                combinedDetails.put("reportUserIdCount", reportUserIdCount);

                combinedDetailsList.add(combinedDetails);
            }

            response.put("pageDetails", pageDetails);
            response.put("combinedDetailsList", combinedDetailsList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in getAllPostReport: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error retrieving post reports", e.getMessage()));
        }
    }
    @Override
    public ResponseEntity<?> getReportsByUserId(ReportPostWebModel postWebModel) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> combinedDetailsList = new ArrayList<>();
        try {
            List<ReportPost> reportPosts = reportRepository.findByUserId(postWebModel.getUserId());
            for (ReportPost reportPost : reportPosts) {
                Posts post = postsRepository.findById(reportPost.getPostId()).orElse(null);
                if (post == null) continue;

                List<FileOutputWebModel> postFiles = mediaFilesService.getMediaFilesByCategoryAndRefId(
                    MediaFileCategory.Post, post.getId());

                Set<String> professionNames = filmProfessionPermanentDetailRepository
                    .getProfessionDataByUserId(post.getUser().getUserId()).stream()
                    .map(FilmProfessionPermanentDetail::getProfessionName)
                    .collect(Collectors.toSet());

                List<FollowersRequest> followersList = friendRequestRepository
                    .findByFollowersRequestReceiverIdAndFollowersRequestIsActive(post.getUser().getUserId(), true);

                Integer userId = userDetails.userInfo().getId();
                boolean likeStatus = likeRepository.findByPostIdAndUserId(post.getId(), userId)
                    .map(Likes::getStatus).orElse(false);

                boolean pinStatus = pinProfileRepository.findByPinProfileIdAndUserId(
                    post.getUser().getUserId(), userId).map(UserProfilePin::isStatus).orElse(false);

                PostWebModel postWebModels = PostWebModel.builder()
                    .id(post.getId())
                    .userId(post.getUser().getUserId())
                    .userName(post.getUser().getName())
                    .postId(post.getPostId())
                    .userProfilePic(userService.getProfilePicUrl(post.getUser().getUserId()))
                    .description(post.getDescription())
                    .pinStatus(pinStatus)
                    .likeCount(post.getLikesCount())
                    .shareCount(post.getSharesCount())
                    .commentCount(post.getCommentsCount())
                    .promoteFlag(post.getPromoteFlag())
                    .postFiles(postFiles)
                    .likeStatus(likeStatus)
                    .privateOrPublic(post.getPrivateOrPublic())
                    .locationName(post.getLocationName())
                    .professionNames(professionNames)
                    .followersCount(followersList.size())
                    .build();

                // Fetch userName for the reporter
                Optional<User> reportUserOpt = userRepository.findById(reportPost.getUserId());
                String reportUserName = reportUserOpt.map(User::getName).orElse("Unknown");

                Map<String, Object> reportDetailsMap = new HashMap<>();
                reportDetailsMap.put("reportPostId", reportPost.getReportPostId());
                reportDetailsMap.put("userId", reportPost.getUserId());
                reportDetailsMap.put("userName", reportUserName); // ✅ Added userName from User table
                reportDetailsMap.put("postId", reportPost.getPostId());
                reportDetailsMap.put("reason", reportPost.getReason());
                reportDetailsMap.put("status", reportPost.getStatus());
                reportDetailsMap.put("createdBy", reportPost.getCreatedBy());
                reportDetailsMap.put("createdOn", reportPost.getCreatedOn());
                reportDetailsMap.put("updatedBy", reportPost.getUpdatedBy());
                reportDetailsMap.put("updatedOn", reportPost.getUpdatedOn());
                reportDetailsMap.put("notificationCount", reportPost.getNotificationCount());
                reportDetailsMap.put("deletePostSuspension", reportPost.getDeletePostSuspension());

                Map<String, Object> combinedDetails = new HashMap<>();
                combinedDetails.put("postWebModel", postWebModels);
                combinedDetails.put("reportDetails", reportDetailsMap);
                combinedDetailsList.add(combinedDetails);
            }

            response.put("combinedDetailsList", combinedDetailsList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in getReportsByUserId: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error retrieving post reports", e.getMessage()));
        }
    }


    @Override
    public ResponseEntity<?> updateReportsByDeleteAnsSuspension(ReportPostWebModel postWebModel) {
        try {
            // Validate input
            if (postWebModel == null || postWebModel.getReportPostId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
            }
            
            // Store violation reason from request - this should be used in case 2
            String violationReason = postWebModel.getViolationReason();

            Optional<ReportPost> optionalReport = reportRepository.findById(postWebModel.getReportPostId());
            if (optionalReport.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Report not found");
            }

            ReportPost report = optionalReport.get();
            report.setDeletePostSuspension(postWebModel.getDeletePostSuspension());
            // Set updatedBy to current admin/user ID - this should come from security context
            // report.setUpdatedBy(getCurrentUserId()); // Replace with actual current user ID
            report.setUpdatedBy(report.getCreatedBy()); // Temporary - should be current admin
            report.setUpdatedOn(new Date());
            reportRepository.save(report);

            // Fetch post details
            Optional<Posts> postOptional = postsRepository.findById(report.getPostId());
            if (postOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
            }

            Posts post = postOptional.get();
            
            // Null safety checks for user and user details
            if (post.getUser() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Post user information not found");
            }

            // Prepare model with email-related details
            postWebModel.setPostTitle(post.getDescription() != null ? post.getDescription() : "Untitled Post");
            postWebModel.setUploadDate(post.getCreatedOn());
            
            String userEmail = post.getUser().getEmail();
            String userName = post.getUser().getName();
            Integer userId = post.getUser().getUserId();
            
            if (userEmail == null || userEmail.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User email not found");
            }
            
            postWebModel.setEmailId(userEmail);
            postWebModel.setUserName(userName != null ? userName : "User");
            postWebModel.setUserId(userId);

            // Send moderation email
            sendModerationEmail(postWebModel);

            return ResponseEntity.ok(new Response(1, "success", "Report updated and email sent successfully"));
            
        } catch (Exception e) {
            // Log the exception for debugging
            // logger.error("Error updating report: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    private void sendModerationEmail(ReportPostWebModel model) {
        try {
            System.out.println("emailId>>>>>>>>>>>>>>>> " + model.getEmailId());
            String to = model.getEmailId();
            String subject = "";
            String body = "";

            // Validate email address
            if (to == null || to.trim().isEmpty()) {
                throw new IllegalArgumentException("Recipient email address is required");
            }

            // Format upload date to readable format
            String formattedDate = model.getUploadDate() != null
                    ? new SimpleDateFormat("dd MMM yyyy").format(model.getUploadDate())
                    : "N/A";

            Integer actionType = model.getDeletePostSuspension();
            if (actionType == null) {
                actionType = 0; // default to warning
            }

            switch (actionType) {
                case 1: // Temporary Suspension
                    subject = "🚫 Temporary Account Suspension Notice from The Film-hook Team";
                    body = String.format(
                        "Dear %s,\n\n" +
                        "We regret to inform you that due to a serious violation of our community standards, " +
                        "your account on the Film-hook platform has been temporarily suspended for a duration of one week.\n\n" +
                        "Post Details:\n" +
                        "- Title/Description: %s\n" +
                        "- Date of Upload: %s\n" +
                        "- Violation Identified: %s\n\n" +
                        "Suspension Period: 7 days from the date of this notice. During this time, you will not be able to " +
                        "log in or access any features of your account.\n\n" +
                        "Please review our Community Guidelines to avoid further issues. If you believe this action was " +
                        "taken in error, you may appeal by contacting our support team.\n\n" +
                        "Best regards,\n" +
                        "The Film-hook Team",
                        model.getUserName(), 
                        model.getPostTitle(), 
                        formattedDate, 
                        model.getViolationReason() != null ? model.getViolationReason() : "Policy violation");
                    break;
                    
                case 2: // Permanent Deletion
                    subject = "❗ Account Termination Notice from Film-hook Team";
                    body = String.format(
                        "Dear %s,\n\n" +
                        "Your account on the Film-hook platform has been permanently terminated due to repeated " +
                        "and/or severe violations of our community guidelines.\n\n" +
                        "Account Information:\n" +
                        "- Email: %s\n" +
                        "- Reason: %s\n" +
                        "- Final Violation Date: %s\n\n" +
                        "This action is final and cannot be reversed. All your content and data have been " +
                        "permanently removed from our platform.\n\n" +
                        "If you believe this action was taken in error, you may contact our appeals team at " +
                        "support@filmhookapps.com within 30 days of this notice.\n\n" +
                        "Best regards,\n" +
                        "The Film-hook Team",
                        model.getUserName(), 
                        model.getEmailId(), 
                        model.getViolationReason() != null ? model.getViolationReason() : "Severe policy violation", 
                        formattedDate);
                    
                    // Fetch user by ID and deactivate
                    if (model.getUserId() != null) {
                        Optional<User> optionalUser = userRepository.findById(model.getUserId());
                        if (optionalUser.isPresent()) {
                            User user = optionalUser.get();
                            user.setStatus(false);
                            userRepository.save(user);
                        }
                    }
                    
                default: // Warning (case 0 and any other values)
                    subject = "⚠️ Content Warning Notice from The Film-hook Team";
                    body = String.format(
                        "Dear %s,\n\n" +
                        "We are writing to inform you that a recent post on your Film-hook account has been reported " +
                        "and found to potentially violate our community standards.\n\n" +
                        "Post Details:\n" +
                        "- Title/Description: %s\n" +
                        "- Date of Upload: %s\n" +
                        "- Issue Identified: %s\n\n" +
                        "This serves as a formal warning. Please review our Community Guidelines to ensure future posts " +
                        "comply with our standards. Repeated violations may result in temporary suspension or permanent " +
                        "termination of your account.\n\n" +
                        "If you have any questions or believe this warning was issued in error, please contact our support team.\n\n" +
                        "Best regards,\n" +
                        "The Film-hook Team",
                        model.getUserName(), 
                        model.getPostTitle(), 
                        formattedDate, 
                        model.getViolationReason() != null ? model.getViolationReason() : "Community guidelines violation");
                    break;
            }

            // Create and send email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("Filmhookmediaapps@gmail.com");
            
            mailSender.send(message);
            
        } catch (Exception e) {
            // Log email sending failure
            // logger.error("Failed to send moderation email to: " + model.getEmailId(), e);
            throw new RuntimeException("Failed to send notification email", e);
        }
    }
}

