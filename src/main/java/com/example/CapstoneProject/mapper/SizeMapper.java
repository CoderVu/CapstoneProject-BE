package com.example.CapstoneProject.mapper;

import com.example.CapstoneProject.request.SizeRequest;
import com.example.CapstoneProject.model.Size;

public class SizeMapper {
    public static Size toSize(SizeRequest sizeRequest) {
       Size size = new Size();
       size.setName(sizeRequest.getName());
       return size;
    }
}
