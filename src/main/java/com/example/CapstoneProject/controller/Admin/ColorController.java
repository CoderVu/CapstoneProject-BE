package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.Request.ColorRequest;
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
                    .body(new APIResponse(Code.CREATED.getCode(), Code.CREATED.getMessage(), true));
        } else {
            return ResponseEntity.status(Code.CONFLICT.getCode())
                    .body(new APIResponse(Code.CONFLICT.getCode(), Code.CONFLICT.getMessage(), false));
        }
    }
}
