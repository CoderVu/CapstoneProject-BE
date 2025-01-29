package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.Request.SizeRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Implement.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/sizes")
@RequiredArgsConstructor
public class SizeController {
    private final SizeService sizeService;

    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> addSize(@RequestBody SizeRequest request) {
        boolean isAdded = sizeService.addSize(request);
        if (isAdded) {
            return ResponseEntity.status(Code.CREATED.getCode())
                    .body(new APIResponse(Code.CREATED.getCode(), Code.CREATED.getMessage(), true));
        } else {
            return ResponseEntity.status(Code.CONFLICT.getCode())
                    .body(new APIResponse(Code.CONFLICT.getCode(), Code.CONFLICT.getMessage(), false));
        }
    }
}
