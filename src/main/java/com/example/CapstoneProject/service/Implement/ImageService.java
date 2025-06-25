package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.Image;
import com.example.CapstoneProject.repository.ImageRepository;
import com.example.CapstoneProject.service.Interface.IImageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class ImageService implements IImageService {

    private final String API_INSERT_URL = "https://in03-196d9c07112f681.serverless.gcp-us-west1.cloud.zilliz.com/v2/vectordb/entities/insert";
    private final String API_UPDATE_URL = "https://in03-196d9c07112f681.serverless.gcp-us-west1.cloud.zilliz.com/v2/vectordb/entities/upsert";
    private final String API_DELETE_URL = "https://in03-196d9c07112f681.serverless.gcp-us-west1.cloud.zilliz.com/v2/vectordb/entities/delete";
    private final String API_GET_URL =    "https://in03-196d9c07112f681.serverless.gcp-us-west1.cloud.zilliz.com/v2/vectordb/entities/get";
    private final String COLLECTION_NAME = "image_collectionn";
    private final String API_TOKEN = "32bddecf36fc12c000f0146b804df9954b0302d0e80c5af70886eac80a8ea55b50a4545d06c7ca39fc99d2583adcc35a056c8079";


    @Autowired
    private ImageRepository imageRepository;
    @Transactional
    @Override
    public boolean sendVectorToAPI(String url, String primaryKey, String imageId, String imageUrl, List<Double> vector, boolean isInsert) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> data = new HashMap<>();

        if (!isInsert) {
            data.put("primary_key", primaryKey); // chỉ dùng cho upsert
        }

        data.put("image_id", imageId);          // luôn là ID hệ thống (id trong bảng Image)
        data.put("image_url", imageUrl);
        data.put("vector", vector);

        Map<String, Object> body = new HashMap<>();
        body.put("collectionName", COLLECTION_NAME);
        body.put("data", Collections.singletonList(data));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_TOKEN);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

//        System.out.println("Response from Zilliz API: " + response.getBody());

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode dataNode = root.path("data");

                JsonNode idsNode = isInsert ? dataNode.path("insertIds") : dataNode.path("upsertIds");
                if (idsNode.isArray() && idsNode.size() > 0) {
                    String returnedId = idsNode.get(0).asText();

                    Image image = imageRepository.findById(imageId).orElse(null);
                    if (image != null) {
                        image.setVectorFeatures(returnedId); // ✅ Luôn cập nhật ID mới nhất
                        imageRepository.save(image);
                    }

                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
//            System.out.println("Zilliz API failed: " + response.getBody());
        }

        return false;
    }



    //    @Transactional
//    @Override
//    public List<Double> getVectorFromZilliz(String primaryKey) {
//        RestTemplate restTemplate = new RestTemplate();
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("collectionName", COLLECTION_NAME);
//        requestBody.put("id", Collections.singletonList(primaryKey));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(API_TOKEN);
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(API_GET_URL, request, String.class);
//            if (response.getStatusCode().is2xxSuccessful()) {
//                ObjectMapper objectMapper = new ObjectMapper();
//                JsonNode root = objectMapper.readTree(response.getBody());
//                JsonNode dataArray = root.path("data");
//
//                if (dataArray.isArray() && dataArray.size() > 0) {
//                    JsonNode vectorNode = dataArray.get(0).path("vector");
//                    List<Double> vector = new ArrayList<>();
//
//                    if (vectorNode.isArray()) {
//                        for (JsonNode num : vectorNode) {
//                            vector.add(num.asDouble());
//                        }
//                        return vector;
//                    }
//                }
//            } else {
//                System.out.println("Zilliz GET vector failed: " + response.getBody());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
    @Transactional
    @Override
    public Map<String, List<Double>> getVectorsFromZilliz(List<String> primaryKeys) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("collectionName", COLLECTION_NAME);
        requestBody.put("id", primaryKeys);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_TOKEN);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        Map<String, List<Double>> resultMap = new HashMap<>();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(API_GET_URL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode dataArray = root.path("data");

                if (dataArray.isArray()) {
                    for (JsonNode item : dataArray) {
                        String primaryKey = item.path("primary_key").asText();
                        JsonNode vectorNode = item.path("vector");

                        List<Double> vector = new ArrayList<>();
                        if (vectorNode.isArray()) {
                            for (JsonNode num : vectorNode) {
                                vector.add(num.asDouble());
                            }
                            resultMap.put(primaryKey, vector);
                        }
                    }
                }
            } else {
                System.out.println("Failed to fetch vectors from Zilliz: " + response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    @Transactional
    @Override
    public boolean upsertVectorToImage(String imageId, List<Double> vector, boolean isUpdate) {
        Image image = imageRepository.findById(imageId).orElse(null);
        if (image == null) return false;

        String imageUrl = image.getUrl();
        String primaryKey = image.getVectorFeatures();

        if (isUpdate) {
            if (primaryKey == null || primaryKey.isBlank()) return false;
            return sendVectorToAPI(API_UPDATE_URL, primaryKey, imageId, imageUrl, vector, false);
        } else {
            return sendVectorToAPI(API_INSERT_URL, null, imageId, imageUrl, vector, true);
        }
    }




    @Transactional
    @Override
    public boolean deleteAllVectorsFromZilliz() {
        List<Image> images = imageRepository.findAll();

        // Lọc ra những vectorFeatures hợp lệ (khác null và không trống)
        List<String> primaryKeys = images.stream()
                .map(Image::getVectorFeatures)
                .filter(Objects::nonNull)
                .filter(key -> !key.isBlank())
                .toList();

        if (primaryKeys.isEmpty()) {
            System.out.println("No vectorFeatures to delete.");
            return false;
        }

        // Tạo filter chuỗi JSON: primary_key in ["..."]
        String filter = "primary_key in [" +
                primaryKeys.stream()
                        .map(id -> "\"" + id + "\"")
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("") +
                "]";

        Map<String, Object> body = new HashMap<>();
        body.put("collectionName", COLLECTION_NAME);
        body.put("filter", filter);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_TOKEN);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(API_DELETE_URL, request, String.class);
            System.out.println("Zilliz delete response: " + response.getBody());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}