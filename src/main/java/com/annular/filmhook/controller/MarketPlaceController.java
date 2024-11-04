package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.MarketPlaceService;
import com.annular.filmhook.webmodel.LikeWebModel;
import com.annular.filmhook.webmodel.MarketPlaceLikeWebModel;
import com.annular.filmhook.webmodel.MarketPlaceWebModel;
import com.annular.filmhook.webmodel.ShootingLocationWebModel;

@RestController
@RequestMapping("/marketPlace")
public class MarketPlaceController {

    @Autowired
    MarketPlaceService marketPlaceService;

    public static final Logger logger = LoggerFactory.getLogger(MarketPlaceController.class);

    @RequestMapping(path = "/marketPlace", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> saveMarketPlace(@ModelAttribute MarketPlaceWebModel marketPlaceWebModel) {
        try {
            logger.info("marketPlace data to be saved :- {}", marketPlaceWebModel);
            return marketPlaceService.saveMarketPlace(marketPlaceWebModel);
        } catch (Exception e) {
            logger.error("marketPlace Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }


    @GetMapping("/getUserMarketPlaces")
    public ResponseEntity<?> getUserMarketPlaces(@RequestParam("userId") Integer userId) {
        try {
            return ResponseEntity.ok().body(marketPlaceService.getUserMarketPlaces(userId));
        } catch (Exception e) {
            logger.error("getMarketPlaceByRentalOrSale Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error at finding user market places...");
        }
    }

    @RequestMapping(path = "/saveShootingLocation", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> saveShootingLocation(@ModelAttribute ShootingLocationWebModel shootingLocationWebModel) {
        try {
            logger.info("shootingLocation data to be saved :- {}", shootingLocationWebModel);
            return marketPlaceService.saveShootingLocation(shootingLocationWebModel);
        } catch (Exception e) {
            logger.error("shootingLocation Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(-1, "Fail", ""));
    }

    @GetMapping("/getSearchMarketPlace")
    public ResponseEntity<?> getSearchMarketPlace(@RequestParam("flag") int flag,
                                                  @RequestParam(value = "searchKey", required = false) String searchKey,
                                                  @RequestParam(value = "rentalOrSale", required = false) Boolean rentalOrSale) {
        try {
            if (flag == 0 && rentalOrSale != null) {
                return marketPlaceService.getMarketPlaceByRentalOrSale(rentalOrSale);
            } else if (flag == 1 && searchKey != null) {
                return marketPlaceService.getSearchMarketPlace(searchKey);
            } else {
                return ResponseEntity.badRequest().body(new Response(400, "Invalid request parameters", ""));
            }
        } catch (Exception e) {
            logger.error("getSearchMarketPlace Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error while reading market places", ""));
        }
    }


    @GetMapping("/getShootingLocation")
    public ResponseEntity<?> getShootingLocation(@RequestParam("flag") int flag,
                                                 @RequestParam(value = "searchKey", required = false) String searchKey) {
        try {
            if (flag == 0) {
                return marketPlaceService.getShootingLocation();
            } else if (flag == 1 && searchKey != null) {
                return marketPlaceService.getSearchShootingLocation(searchKey);
            } else {
                return ResponseEntity.badRequest().body(new Response(400, "Invalid request parameters", ""));
            }
        } catch (Exception e) {
            logger.error("getShootingLocation Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error while reading shooting locations", ""));
        }
    }
    @GetMapping("/getMarketPlaceByMarketTypeByUserId")
    public ResponseEntity<?> getSearchMarketPlace(@RequestParam("marketType") String marketType) {
        try {
            
                return marketPlaceService.getMarketPlaceByMarketTypeByUserId(marketType);
          
        } catch (Exception e) {
            logger.error("getMarketPlaceByMarketTypeByUserId Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Error while reading market places", ""));
        }
    }
    
    @PostMapping("/addMarketPlaceLike")
    public ResponseEntity<?> addMarketPlaceLike(@RequestBody MarketPlaceLikeWebModel marketPlaceLikeWebModel) {
        try {
            MarketPlaceLikeWebModel likeWebModelOutput = marketPlaceService.addMarketPlaceLike(marketPlaceLikeWebModel);
            if (likeWebModelOutput != null) return ResponseEntity.ok(new Response(1, "Likes add/updated for the post successfully...", likeWebModelOutput));
        } catch (Exception e) {
            logger.error("marketPlaceLikeWebModel Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.badRequest().body(new Response(-1, "Fail", ""));
    }


}
