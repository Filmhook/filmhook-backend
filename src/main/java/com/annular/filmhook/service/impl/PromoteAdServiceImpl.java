package com.annular.filmhook.service.impl;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.PromoteAd;
import com.annular.filmhook.model.PromoteAd.PromoteStatus;
import com.annular.filmhook.model.PromoteMediaFiles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.VisitPage;
import com.annular.filmhook.repository.MediaFilesRepository;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.PromoteAdRepository;
import com.annular.filmhook.repository.PromoteMediaFilesRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.repository.VisitPageRepository;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.PostService;
import com.annular.filmhook.service.PromoteAdService;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.PromoteWebModel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromoteAdServiceImpl implements PromoteAdService {

	private final PromoteAdRepository promoteAdRepository;
	private final PostsRepository postsRepository;
	private final VisitPageRepository visitPageRepository;
	private final MediaFilesService mediaFilesService;
	private final UserRepository userRepository;
	@Autowired
	PostService postService;
	@Autowired
	private PromoteMediaFilesRepository promoteMediaFilesRepo;
	@Autowired
	private MediaFilesRepository mediaFilesRepository;

	@Override
	public Response savePromote(PromoteWebModel model, Integer userId) {

		User user = userRepository.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		Integer postId;
		
	    PromoteAd promoteId;
		 if (model.getPromoteId() != null && model.getPromoteId() > 0) {

			 promoteId = promoteAdRepository.findById(model.getPromoteId())
		                .orElseThrow(() -> new RuntimeException("Promote record not found"));

		        // ---------------------------------
		        // UPDATE ONLY NON-NULL FIELDS
		        // ---------------------------------
		        if (model.getHeadline() != null) promoteId.setHeadline(model.getHeadline());
		        if (model.getPromoteDescription() != null) promoteId.setPromoteDescription(model.getPromoteDescription());
		        if (model.getBusinessLocation() != null) promoteId.setBusinessLocation(model.getBusinessLocation());
		        if (model.getBusinessType() != null) promoteId.setBusinessType(model.getBusinessType());
		        if (model.getAdvObject() != null) promoteId.setAdvObject(model.getAdvObject());
		        if (model.getAdvObjectValue() != null) promoteId.setAdvObjectValue(model.getAdvObjectValue());
		        if (model.getBusinessName() != null) promoteId.setBusinessName(model.getBusinessName());
		        if (model.getBudget() != null) promoteId.setBudget(model.getBudget());
		        if (model.getDays() != null) promoteId.setDays(model.getDays());
		        if (model.getTargetCountries() != null) promoteId.setTargetCountries(model.getTargetCountries());
		        if (model.getReachMin() != null) promoteId.setReachMin(model.getReachMin());
		        if (model.getReachMax() != null) promoteId.setReachMax(model.getReachMax());
		        if (model.getAmount() != null) promoteId.setAmount(model.getAmount());
		        if (model.getTotalCost() != null) promoteId.setTotalCost(model.getTotalCost());
		        if (model.getTaxFee() != null) promoteId.setTaxFee(model.getTaxFee());
		        if (model.getCgst() != null) promoteId.setCgst(model.getCgst());
		        if (model.getSgst() != null) promoteId.setSgst(model.getSgst());
		        if (model.getPrice() != null) promoteId.setPrice(model.getPrice());

		        // ---------------------------------
		        // Update logo
		        // ---------------------------------
		        if (model.getCompanyLogo() != null && !model.getCompanyLogo().isEmpty()) {

		            FileInputWebModel fm = FileInputWebModel.builder()
		                    .userId(userId)
		                    .category(MediaFileCategory.Promote)
		                    .categoryRefId(promoteId.getPost().getId())
		                    .files(List.of(model.getCompanyLogo()))
		                    .build();

		            mediaFilesService.saveMediaFiles(fm, user);
		            promoteId.setCompanyLogo(model.getCompanyLogo().getOriginalFilename());
		        }

		        // ---------------------------------
		        // Update business address doc
		        // ---------------------------------
		        if (model.getBusinessAddressDoc() != null && !model.getBusinessAddressDoc().isEmpty()) {

		            FileInputWebModel doc = FileInputWebModel.builder()
		                    .userId(userId)
		                    .category(MediaFileCategory.PromoteDocs)
		                    .categoryRefId(promoteId.getPost().getId())
		                    .files(List.of(model.getBusinessAddressDoc()))
		                    .build();

		            mediaFilesService.saveMediaFiles(doc, user);
		            promoteId.setBusinessAddress(model.getBusinessAddressDoc().getOriginalFilename());
		        }

		        // ---------------------------------
		        // RESET PAYMENT + LIFECYCLE
		        // ---------------------------------
		        promoteId.setPaymentStatus("PENDING");
		        promoteId.setTransactionId(null);
		        promoteId.setStatus(PromoteStatus.NotStarted);
		        promoteId.setStartDate(null);
		        promoteId.setEndDate(null);
		        
		        PromoteAd save = promoteAdRepository.save(promoteId);

		        return new Response(1, "Success", null);
		    }
		// ==================================================
		// CASE 1: Existing post
		// ==================================================
		if (model.getPostId() != null && model.getPostId() > 0) {

			postsRepository.findById(model.getPostId())
			.orElseThrow(() -> new RuntimeException("Post not found"));

			postId = model.getPostId();

		} else {

			// ==================================================
			// CASE 2: No post → create a fresh post
			// ==================================================
			PostWebModel postModel = new PostWebModel();
			postModel.setUserId(model.getUserId());
			postModel.setFiles(model.getFiles());
			postModel.setDescription(model.getDescription());
			postModel.setPostLinkUrl(model.getPostLinkUrl());
			postModel.setLatitude(model.getLatitude());
			postModel.setLongitude(model.getLongitude());
			postModel.setAddress(model.getAddress());
			postModel.setPrivateOrPublic(model.getPrivateOrPublic());

			PostWebModel savedPost = postService.savePostsWithFiles(postModel);
			postId = savedPost.getId();
		}

		Posts post = postsRepository.findById(postId)
				.orElseThrow(() -> new RuntimeException("Post not found after creation"));


		// ==================================================
		// CREATE PROMOTION ENTRY
		// ==================================================
		PromoteAd promote = PromoteAd.builder()
				.post(post)
				.headline(model.getHeadline())
				.promoteDescription(model.getPromoteDescription())
				.businessLocation(model.getBusinessLocation())
				.businessType(model.getBusinessType())
				.advObject(model.getAdvObject())
				.advObjectValue(model.getAdvObjectValue())
				.businessName(model.getBusinessName())
				.budget(model.getBudget())
				.days(model.getDays())
				.targetCountries(model.getTargetCountries())
				.reachMin(model.getReachMin())
				.reachMax(model.getReachMax())
				.paymentStatus("PENDING")
				.status(PromoteAd.PromoteStatus.NotStarted)
				.transactionId(null)
				.amount(model.getAmount())
				.totalCost(model.getTotalCost())
				.taxFee(model.getTaxFee())
				.cgst(model.getCgst())
				.sgst(model.getSgst())
				.price(model.getPrice())
				.build();


		// ==================================================
		// SET VISIT TYPE
		// ==================================================
		if (model.getVisitTypeId() != null) {
			VisitPage visitPage = visitPageRepository.findById(model.getVisitTypeId())
					.orElseThrow(() -> new RuntimeException("Visit Type not found"));
			promote.setVisitType(visitPage);
		}


		// ==================================================
		// SAVE COMPANY LOGO
		// ==================================================
		if (model.getCompanyLogo() != null && !model.getCompanyLogo().isEmpty()) {

			FileInputWebModel fileInput = FileInputWebModel.builder()
					.userId(userId)
					.category(MediaFileCategory.Promote)
					.categoryRefId(postId)
					.files(List.of(model.getCompanyLogo()))
					.build();

			mediaFilesService.saveMediaFiles(fileInput, user);
			promote.setCompanyLogo(model.getCompanyLogo().getOriginalFilename());
		}


		// ==================================================
		// SAVE BUSINESS ADDRESS DOCUMENT
		// ==================================================
		if (model.getBusinessAddressDoc() != null &&
				!model.getBusinessAddressDoc().isEmpty()) {

			FileInputWebModel docInput = FileInputWebModel.builder()
					.userId(userId)
					.category(MediaFileCategory.PromoteDocs)
					.categoryRefId(postId)
					.files(List.of(model.getBusinessAddressDoc()))
					.build();

			mediaFilesService.saveMediaFiles(docInput, user);
			promote.setBusinessAddress(model.getBusinessAddressDoc().getOriginalFilename());
		}


		// SAVE PROMOTE ENTRY
		PromoteAd savedPromote = promoteAdRepository.save(promote);


		// ==================================================================
		// CASE: Existing Post + New Uploaded Photos → save as POST + PROMOTE
		// ==================================================================
		if (model.getFiles() != null && !model.getFiles().isEmpty()) {

			FileInputWebModel uploadInput = FileInputWebModel.builder()
					.userId(userId)
					.category(MediaFileCategory.Post)
					.categoryRefId(postId)
					.files(model.getFiles())
					.build();

			// saveMediaFilesAndReturn → return list<MediaFiles>
			mediaFilesService.saveMediaFiles(uploadInput, user);


		}


		// ==========================================================
		// CASE: SELECTED MEDIA IDS COMING FROM FRONT-END
		// ==========================================================
		if (model.getSelectedMediaIds() != null && !model.getSelectedMediaIds().isEmpty()) {

			// Remove old entries (edit case)
			promoteMediaFilesRepo.deleteByPromote_PromoteId(savedPromote.getPromoteId());

			for (Integer mediaId : model.getSelectedMediaIds()) {

				MediaFiles media = mediaFilesRepository.findById(mediaId)
						.orElseThrow(() -> new RuntimeException("Media file not found: " + mediaId));

				PromoteMediaFiles pm = PromoteMediaFiles.builder()
						.promote(savedPromote)
						.mediaFile(media)
						.selected(true)
						.build();

				promoteMediaFilesRepo.save(pm);
			}
		}


		return new Response(1, "Success", null);
	}

	@Override
	public PromoteAd getPromoteByPostId(Integer postId) {
		return promoteAdRepository.findByPost_Id(postId);
	}

	@Override
	public Response updatePaymentSuccess(PromoteWebModel model) {

		PromoteAd promote = promoteAdRepository.findById(model.getPromoteId())
				.orElseThrow(() -> new RuntimeException("Promote record not found"));

		promote.setPaymentStatus("SUCCESS");
		promote.setStatus(PromoteStatus.Running);
		promote.setTransactionId(model.getTransactionId());

		Date start = new Date();
		promote.setStartDate(start);

		// calculate end date
		Date endDate = Date.from(
				start.toInstant().plus(Duration.ofDays(promote.getDays()))
				);
		promote.setEndDate(endDate);

		// update post flag
		Posts post = promote.getPost();
		post.setPromoteFlag(true);
		postsRepository.save(post);

		promoteAdRepository.save(promote);

		return new Response(1, "Payment Success", null);
	}


	@Override
	public Response updatePaymentFailed(PromoteWebModel model) {

		PromoteAd promote = promoteAdRepository.findById(model.getPromoteId())
				.orElseThrow(() -> new RuntimeException("Promote record not found"));

		promote.setPaymentStatus("FAILED");
		promote.setTransactionId(model.getTransactionId());
		promote.setStatus(PromoteStatus.NotStarted); // reset status if needed

		Posts post = promote.getPost();
		post.setPromoteFlag(false);
		postsRepository.save(post);

		PromoteAd updated = promoteAdRepository.save(promote);

		return new Response(-1, "Payment Failed", null);
	}


	@Override
	public Response getRecentPromotions(Integer userId) {

		List<PromoteAd> list = promoteAdRepository.findRecentRunningOrCompletedByUserId(userId);

		List<PromoteWebModel> response = list.stream().map(promote -> {

			List<FileOutputWebModel> postFiles =
					mediaFilesService.getMediaFilesByCategoryAndRefId(
							MediaFileCategory.Post,
							promote.getPost().getId()
							);

			return PromoteWebModel.builder()
					.promoteId(promote.getPromoteId())
					.headline(promote.getHeadline())
					.promoteDescription(promote.getPromoteDescription())
					.businessName(promote.getBusinessName())
					.businessType(promote.getBusinessType())
					.advObject(promote.getAdvObject())
					.advObjectValue(promote.getAdvObjectValue())
					.status(promote.getStatus())  
					.createdOn(promote.getCreatedOn())
					.postId(promote.getPost().getId())
					.postDescription(promote.getPost().getDescription())
					.postFiles(postFiles)
					.build();

		}).collect(Collectors.toList());

		return new Response(1, "Recent promoted posts", response);
	}

	@Override
	public Response updateBeforePayment(PromoteWebModel model) {

		PromoteAd promote = promoteAdRepository.findById(model.getPromoteId())
				.orElseThrow(() -> new RuntimeException("Promote record not found"));

		// ====== UPDATE ONLY IF NON-NULL ======
		if (model.getHeadline() != null)
			promote.setHeadline(model.getHeadline());

		if (model.getPromoteDescription() != null)
			promote.setPromoteDescription(model.getPromoteDescription());

		if (model.getBusinessLocation() != null)
			promote.setBusinessLocation(model.getBusinessLocation());

		if (model.getBusinessType() != null)
			promote.setBusinessType(model.getBusinessType());

		if (model.getAdvObject() != null)
			promote.setAdvObject(model.getAdvObject());

		if (model.getAdvObjectValue() != null)
			promote.setAdvObjectValue(model.getAdvObjectValue());

		if (model.getBusinessName() != null)
			promote.setBusinessName(model.getBusinessName());

		if (model.getBudget() != null)
			promote.setBudget(model.getBudget());

		if (model.getDays() != null)
			promote.setDays(model.getDays());

		if (model.getTargetCountries() != null)
			promote.setTargetCountries(model.getTargetCountries());

		if (model.getReachMin() != null)
			promote.setReachMin(model.getReachMin());

		if (model.getReachMax() != null)
			promote.setReachMax(model.getReachMax());

		if (model.getAmount() != null)
			promote.setAmount(model.getAmount());

		if (model.getTotalCost() != null)
			promote.setTotalCost(model.getTotalCost());

		if (model.getTaxFee() != null)
			promote.setTaxFee(model.getTaxFee());

		if (model.getCgst() != null)
			promote.setCgst(model.getCgst());

		if (model.getSgst() != null)
			promote.setSgst(model.getSgst());

		if (model.getPrice() != null)
			promote.setPrice(model.getPrice());

		// ====== UPDATE LOGO FILE ======
		if (model.getCompanyLogo() != null && !model.getCompanyLogo().isEmpty()) {

			FileInputWebModel fileModel = FileInputWebModel.builder()
					.userId(promote.getPost().getUser().getUserId())
					.category(MediaFileCategory.Promote)
					.categoryRefId(promote.getPost().getId())
					.files(List.of(model.getCompanyLogo()))
					.build();

			mediaFilesService.saveMediaFiles(fileModel, promote.getPost().getUser());

			promote.setCompanyLogo(model.getCompanyLogo().getOriginalFilename());
		}

		// ====== UPDATE BUSINESS DOC ======
		if (model.getBusinessAddressDoc() != null 
				&& !model.getBusinessAddressDoc().isEmpty()) {

			FileInputWebModel docModel = FileInputWebModel.builder()
					.userId(promote.getPost().getUser().getUserId())
					.category(MediaFileCategory.PromoteDocs)
					.categoryRefId(promote.getPost().getId())
					.files(List.of(model.getBusinessAddressDoc()))
					.build();

			mediaFilesService.saveMediaFiles(docModel, promote.getPost().getUser());

			promote.setBusinessAddress(model.getBusinessAddressDoc().getOriginalFilename());
		}

		// ====== RESET PAYMENT STATE ======
		promote.setPaymentStatus("PENDING");
		promote.setTransactionId(null);

		// ====== RESET PROMOTE LIFECYCLE ======
		promote.setStatus(PromoteStatus.NotStarted);
		promote.setStartDate(null);
		promote.setEndDate(null);

		PromoteAd updated = promoteAdRepository.save(promote);

		return new Response(1, "Promote updated successfully", updated);
	}



}

