package com.annular.filmhook.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.ActionService;
import com.annular.filmhook.webmodel.CommentWebModel;
import com.annular.filmhook.webmodel.LikeWebModel;
import com.annular.filmhook.webmodel.ShareWebModel;


@RestController
@RequestMapping("/action")
public class ActionController {

    @Autowired
    ActionService actionService;

    public static final Logger logger = LoggerFactory.getLogger(ActionController.class);

    @PostMapping("/addLike")
    public ResponseEntity<?> addLike(@RequestBody LikeWebModel likeWebModel) {
        try {
            logger.info("addLike controller start");
            return actionService.addLike(likeWebModel);
        } catch (Exception e) {
            logger.error("addLike Method Exception {}" + e);
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/updateLike")
    public ResponseEntity<?> updateLike(@RequestBody LikeWebModel likeWebModel) {
        try {
            logger.info("updateLike controller start");
            return actionService.updateLike(likeWebModel);
        } catch (Exception e) {
            logger.error("updateLike Method Exception {}" + e);
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/addComment")
    public ResponseEntity<?> addComment(@RequestBody CommentWebModel commentWebModel) {
        try {
            logger.info("addComment controller start");
            return actionService.addComment(commentWebModel);
        } catch (Exception e) {
            logger.error("addComment Method Exception {}" + e);
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/getComment")
    public ResponseEntity<?> getComment(@RequestBody CommentWebModel commentWebModel) {
        try {
            logger.info("getComment controller start");
            return actionService.getComment(commentWebModel);
        } catch (Exception e) {
            logger.error("getComment Method Exception {}" + e);
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/deleteComment")
    public ResponseEntity<?> deleteComment(@RequestBody CommentWebModel commentWebModel) {
        try {
            logger.info("deleteComment controller start");
            return actionService.deleteComment(commentWebModel);
        } catch (Exception e) {
            logger.error("deleteComment Method Exception {}" + e);
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @PostMapping("/addShare")
    public ResponseEntity<?> addShare(@RequestBody ShareWebModel shareWebModel) {
        try {
            logger.info("addShare controller start");
            return actionService.addShare(shareWebModel);
        } catch (Exception e) {
            logger.error("addShare Method Exception {}" + e);
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

}
	


