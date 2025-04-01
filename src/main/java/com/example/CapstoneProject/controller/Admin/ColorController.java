package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.request.ColorRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.IColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/colors")
@RequiredArgsConstructor
public class ColorController {
    private final IColorService colorService;

    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> addSize(@RequestBody ColorRequest request) {
        boolean isAdded = colorService.addColor(request);
        if (isAdded) {
            return ResponseEntity.status(Code.CREATED.getCode())
                    .body(new APIResponse(Code.CREATED.getCode(), Code.CREATED.getMessage(), ""));
        } else {
            return ResponseEntity.status(Code.CONFLICT.getCode())
                    .body(new APIResponse(Code.CONFLICT.getCode(), Code.CONFLICT.getMessage(), ""));
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse> updateSize(@PathVariable String id, @RequestBody ColorRequest request) {
        boolean isUpdated = colorService.updateColor(id, request);
        if (isUpdated) {
            return ResponseEntity.status(Code.OK.getCode())
                    .body(new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), ""));
        } else {
            return ResponseEntity.status(Code.NOT_FOUND.getCode())
                    .body(new APIResponse(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(), ""));
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> deleteSize(@PathVariable String id) {
        boolean isDeleted = colorService.deleteColor(id);
        if (isDeleted) {
            return ResponseEntity.status(Code.OK.getCode())
                    .body(new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), ""));
        } else {
            return ResponseEntity.status(Code.NOT_FOUND.getCode())
                    .body(new APIResponse(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(), ""));
        }
    }
}
