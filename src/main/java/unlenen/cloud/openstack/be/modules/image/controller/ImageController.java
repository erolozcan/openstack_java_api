package unlenen.cloud.openstack.be.modules.image.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import unlenen.cloud.openstack.be.exception.UnvalidCallException;
import unlenen.cloud.openstack.be.model.response.ErrorInfo;
import unlenen.cloud.openstack.be.model.response.OpenStackResponse;
import unlenen.cloud.openstack.be.modules.image.models.ImageContainerFormat;
import unlenen.cloud.openstack.be.modules.image.models.ImageDiskFormat;
import unlenen.cloud.openstack.be.modules.image.service.ImageService;

/**
 *
 * @author Nebi Volkan UNLENEN
 */
@RestController
@RequestMapping("/image/v2.0")
public class ImageController {

    @Autowired
    ImageService imageService;

    @GetMapping("/images")
    public ResponseEntity<OpenStackResponse> getImages(
            @RequestHeader("token") String token,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "tags") String tags
    ) {
        OpenStackResponse openStackResponse = new OpenStackResponse();
        HttpStatus httpStatus;
        try {
            openStackResponse.setOpenStackResult(imageService.getImages(token, name, tags));
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = handleError(openStackResponse, e);
        }
        return new ResponseEntity<OpenStackResponse>(openStackResponse, httpStatus);
    }

    @PostMapping("/image/{name}")
    public ResponseEntity<OpenStackResponse> createImage(
            @RequestHeader("token") String token,
            @PathVariable() String name,
            @RequestParam(required = true, name = "diskFormat") ImageDiskFormat diskFormat,
            @RequestParam(required = true, name = "containerFormat") ImageContainerFormat containerFormat
    ) {
        OpenStackResponse openStackResponse = new OpenStackResponse();
        HttpStatus httpStatus;
        try {
            openStackResponse.setOpenStackResult(imageService.createImage(token, name, diskFormat, containerFormat));
            httpStatus = HttpStatus.CREATED;
        } catch (Exception e) {
            httpStatus = handleError(openStackResponse, e);
        }
        return new ResponseEntity<OpenStackResponse>(openStackResponse, httpStatus);
    }

    @DeleteMapping("/image/{imageId}")
    public ResponseEntity<OpenStackResponse> deleteImage(
            @RequestHeader("token") String token,
            @PathVariable() String imageId
    ) {
        OpenStackResponse openStackResponse = new OpenStackResponse();
        HttpStatus httpStatus;
        try {
            imageService.deleteImage(token, imageId);
            httpStatus = HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            httpStatus = handleError(openStackResponse, e);
        }
        return new ResponseEntity<OpenStackResponse>(openStackResponse, httpStatus);
    }

    @PutMapping("/v2/images/{image_id}/tags/{tag}")
    public ResponseEntity<OpenStackResponse> addImageTag(
            @RequestHeader("token") String token,
            @PathVariable() String image_id,
            @PathVariable() String tag

    ) {
        OpenStackResponse openStackResponse = new OpenStackResponse();
        HttpStatus httpStatus;
        try {
            imageService.addImageTag(token,image_id,tag);
            httpStatus = HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            httpStatus = handleError(openStackResponse, e);
        }
        return new ResponseEntity<OpenStackResponse>(openStackResponse, httpStatus);
    }

    @DeleteMapping("/v2/images/{image_id}/tags/{tag}")
    public ResponseEntity<OpenStackResponse> deleteImageTag(
            @RequestHeader("token") String token,
            @PathVariable() String image_id,
            @PathVariable() String tag

    ) {
        OpenStackResponse openStackResponse = new OpenStackResponse();
        HttpStatus httpStatus;
        try {
            imageService.deleteImageTag(token,image_id,tag);
            httpStatus = HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            httpStatus = handleError(openStackResponse, e);
        }
        return new ResponseEntity<OpenStackResponse>(openStackResponse, httpStatus);
    }
    
    private HttpStatus handleError(OpenStackResponse openStackResponse, Exception e) {
        HttpStatus httpStatus;
        openStackResponse.setError(new ErrorInfo(e.getClass().getSimpleName(), e.getMessage()));
        if (e instanceof UnvalidCallException) {
            httpStatus = ((UnvalidCallException) e).getCurrentStatusCode();
        } else {
            httpStatus = HttpStatus.UNAUTHORIZED;
        }
        return httpStatus;
    }

}
