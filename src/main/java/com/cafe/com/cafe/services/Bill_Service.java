package com.cafe.com.cafe.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.pdfbox.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.cafe.com.cafe.Entites.Bill;
import com.cafe.com.cafe.Entites.Order;
import com.cafe.com.cafe.JWT.JwtFilter;
import com.cafe.com.cafe.PDF.GeneratePdf;
import com.cafe.com.cafe.constants.Cafe_Constants;
import com.cafe.com.cafe.repositories.Bill_Dao;
import com.cafe.com.cafe.repositories.Order_Dao;
import com.cafe.com.cafe.service_Interfaces.Bill_Service_interface;
import com.cafe.com.cafe.utils.CafeUtils;
import com.cafe.com.cafe.utils.EmailUtils;
import com.cafe.com.cafe.wrapper.TransactionDetails;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Bill_Service implements Bill_Service_interface {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    Bill_Dao billDao;

    @Autowired
    Order_Dao order_Dao;

    @Autowired
    JavaMailSender javaMailSender;
    // --------------------------------------------------------------------------------------------------------------
    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside generateReport");
        try {

            String fileName; // uuid

            Bill insertBills = null;
            if (validateRequestMap(requestMap)) {
                // retrieves a certain bill stored in the database
                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
                    fileName = (String) requestMap.get("uuid");
                }
                // creates a new bill
                else {
                    fileName = CafeUtils.getUUID();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }

//                GeneratePdf.generatePDF(insertBills, fileName);

                return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity(Cafe_Constants.MISSING_REQUIRED_DATA, HttpStatus.BAD_REQUEST);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Bill insertBill(Map<String, Object> requestMap) {
        try {

            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setProductDetail((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            bill.setPaymentstatus((String) requestMap.get("paymentStatus"));
            bill.setTableNumber((String) requestMap.get("tableNumber"));

            List<Order> list = new ArrayList<>();
            String order = bill.getProductDetail();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(order);

            if (jsonNode.isArray()) {
                for (JsonNode item : jsonNode) {

                    String name = item.get("name").asText();
                    String category = item.get("category").asText();
                    int quantity = item.get("quantity").asInt();
                    int price = item.get("price").asInt();

                    Order orders = new Order();
                    orders.setProductName(name);
                    orders.setCategory(category);
                    orders.setQuantity(String.valueOf(quantity));   
                    orders.setPrice(String.valueOf(price));
                    int totalprice = quantity * (int) price;
                    orders.setTotal(Integer.parseInt(String.valueOf(totalprice)));
                    orders.setTablenumber((String) requestMap.get("tableNumber"));
                    orders.setBill_uuid(bill.getUuid());
                    orders.setPaymentMethod((String) requestMap.get("paymentMethod"));
                    orders.setCreatedBy(jwtFilter.getCurrentUser());
                    
                    list.add(orders);
                }
            }

            bill.setOrders(list);

            Bill save = billDao.save(bill);
            return save;
        } catch (Exception ex) {
            log.error("Error while inserting Bill and Orders", ex);
        }
        return new Bill();
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("paymentMethod")
                && requestMap.containsKey("productDetails") && requestMap.containsKey("totalAmount");
    }

    // --------------------------------------------------------------------------------------------------------------
    // get bills from database and send to user or admin
    @Override
    public ResponseEntity<List<Bill>> getBills() {
        try {

            List<Bill> list = new ArrayList<>();
            if (jwtFilter.isAdmin()) {
                list = this.billDao.getAllBills();
            } else {
                list = this.billDao.getBillByUserName(jwtFilter.getCurrentUser());
            }
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    public ResponseEntity<byte[]> getpdf(Map<String, String> requestMap) {
        log.info("Inside getPdf : requestMap {}", requestMap);
        try {
            byte[] byteArray = new byte[0]; // array of binary data which will be converted into ascii data in the ui
            if(requestMap.containsKey("id") && requestMap.containsKey("uuid")){
                // no specified uuid --> cannot retrieve
                if (!requestMap.containsKey("uuid")) {
                    return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
                }
                String filePath = Cafe_Constants.STORE_LOCATION + "/" + (String) requestMap.get("uuid") + ".pdf";
                if (CafeUtils.isFileExist(filePath)) {
                    makebill(requestMap);
                    byteArray = getByteArray(filePath);
                    return new ResponseEntity<>(byteArray, HttpStatus.OK);
                } else {
                    // if the file path doesn't exist, generate a new bill for it
                    makebill(requestMap);
                    byteArray = getByteArray(filePath);
                    return new ResponseEntity<>(byteArray, HttpStatus.OK);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void makebill(Map<String, String> requestMap) {
        try {
            int id = Integer.parseInt(requestMap.get("id"));
            Bill bill = billDao.findById(id).get();
            System.out.println(bill);
            GeneratePdf.generatePDF(bill, bill.getUuid());
        } catch (Exception ex) {
            log.error("Error while inserting Bill and Orders", ex);
        }
    }

    // converts text in the pdf into a byte array
    private byte[] getByteArray(String filePath) throws Exception {
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream); // writes each line as a byte array
        targetStream.close();
        return byteArray;
    }

    // ------------------------------------------------------------------------------------------------------------------
    // this method is used to deletebill
    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            // only admins can delete bills
            if (jwtFilter.isAdmin()) {
                @SuppressWarnings("rawtypes")
                Bill bill = billDao.findById(id).get();
                String filePath = Cafe_Constants.STORE_LOCATION + "/" + (String) bill.getUuid() + ".pdf";
                if (bill != null) {
                    billDao.deleteById(id);
                    File file = new File(filePath);
                    file.delete();
                    // delete bill with the specified id
                    return CafeUtils.getResponseEntity(Cafe_Constants.BILL_DELETED, HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_BILL, HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity(Cafe_Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static final String KEY = "rzp_test_DqGMseHxxRaQMg";
    private static final String KEY_SECRET = "ALlStD4UylPMlFPsyVSuhVrm";
    private static final String CURRENCY = "INR";
 
    @Override
    public TransactionDetails createTransaction(Double amount) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("amount", (amount * 100));
            jsonObject.put("currency", CURRENCY);

            RazorpayClient razorpayClient = new RazorpayClient(KEY, KEY_SECRET);

            com.razorpay.Order order = razorpayClient.orders.create(jsonObject);
            TransactionDetails transactionDetails = prepareTransactionDetails(order);
            
            return transactionDetails;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    private TransactionDetails prepareTransactionDetails(com.razorpay.Order order) {
        
        String orderId = order.get("id");
        String currency = order.get("currency");
        Integer amount = order.get("amount");
        TransactionDetails transactionDetails = new TransactionDetails(orderId, currency, amount);
        return transactionDetails;
    }

    @Override
    public ResponseEntity<List<Order>> getAllorders() {
        try {
            if (jwtFilter.isAdmin()) {
                List<Order> all = order_Dao.findAllOrders();
                return new ResponseEntity<>(all, HttpStatus.OK);
            } else {
                String s = jwtFilter.getCurrentUser();
                List<Order> all = order_Dao.getOrdeByUserName(s);
                return new ResponseEntity<>(all, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Order>> getAllOrdersByDate() {
        try {
            
            List<Order> all = order_Dao.findAllOrdersByDate();
            
            if (!all.isEmpty()) {
                return new ResponseEntity<>(all, HttpStatus.OK);
            }
            
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
   
    @Override
    public ResponseEntity<String> updateOrderStatus(Map<String, String> request) {
        try {
            // Extract status and id from the request
            String statusString = request.get("orderStatus");
            String idString = request.get("id");

            if (statusString != null && idString != null) {
                // Parse status as boolean and id as integer
                boolean newStatus = Boolean.parseBoolean(statusString);
                Integer orderId = Integer.parseInt(idString);

                // Assuming you have an OrderRepository to interact with the database
                Order order = order_Dao.findById(orderId).orElse(null);

                if (order != null) {
                    // Update the order status
                    order.setOrderStatus(newStatus);
                    order_Dao.save(order);

                    return CafeUtils.getResponseEntity("Order status updated successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Order not found", HttpStatus.NOT_FOUND);
                }
            } else {
                return CafeUtils.getResponseEntity("Invalid request format", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteOrder(Integer id) {
        try {
            @SuppressWarnings("rawtypes")
            Optional optional = order_Dao.findById(id);
            if (!optional.isEmpty()) {
                order_Dao.deleteById(id);
                // delete bill with the specified id
                return CafeUtils.getResponseEntity(Cafe_Constants.ORDER_DELETE, HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_ORDER, HttpStatus.BAD_REQUEST);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @Override
    public ResponseEntity<List<Order>> getOrderByCategory(Map<String, String> requestMap) {
        try {
            String category = requestMap.get("category");
            if (category != null) {
                List<Order> all = order_Dao.findByCategory(category);
                if (!all.isEmpty()) {
                    return new ResponseEntity<>(all, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @Override
    public ResponseEntity<String> sendBill(Map<String, String> requestMap) {
        try {
            if (requestMap.containsKey("email") && requestMap.containsKey("uuid")) {
                String filePath = Cafe_Constants.STORE_LOCATION + "/" + (String) requestMap.get("uuid") + ".pdf";
                File file = new File(filePath);
                if (EmailUtils.sendBill(requestMap.get("email"),"NamasteVillage",file)) {
                    return CafeUtils.getResponseEntity(Cafe_Constants.EMAIL_SENDE, HttpStatus.OK);
                }              
                return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_EMAIL, HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
   
    
    
}