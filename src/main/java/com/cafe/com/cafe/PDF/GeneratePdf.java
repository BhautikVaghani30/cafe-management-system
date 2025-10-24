package com.cafe.com.cafe.PDF;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import com.cafe.com.cafe.Entites.Bill;
import com.cafe.com.cafe.Entites.Order;
import com.cafe.com.cafe.constants.Cafe_Constants;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneratePdf {
    public static void generatePDF(Bill requestMap, String fileName)

            throws DocumentException, MalformedURLException, IOException {
        System.out.println("insaid generate report : " + requestMap);
        String data = "invoiceNumber:- " + fileName + "\n" + "CustomerName:- " + requestMap.getName()
                + "\n" + "Contact Number:- " + requestMap.getContactNumber()
                + "\n" + "Payment Method:- " + requestMap.getPaymentMethod()
                + "\n" + "paymentStatus:- " + requestMap.getPaymentstatus()
                + "\n" + "TableNumber:- " + requestMap.getTableNumber();

        // create pdf document using itextpdf
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(Cafe_Constants.STORE_LOCATION + "/" + fileName + ".pdf")); // path to the
        // folder
        // storing the
        // bills

        document.open(); // open pdf document for writing
        setRectangleInPdf(document);

        Paragraph chunk = new Paragraph("Namaste Village Cafe", getFont("Header"));
        chunk.setAlignment(Element.ALIGN_CENTER);
        document.add(chunk);
        chunk.setSpacingAfter(25f);

        Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
        paragraph.setSpacingBefore(10f);
        document.add(paragraph);
        paragraph.setSpacingAfter(10f);

        LocalDate localDate = LocalDate.now();
        Paragraph date = new Paragraph("Date : " + localDate, getFont("Data"));
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);
        date.setSpacingAfter(10f);

        PdfPTable table = new PdfPTable(5); // table for the purchased items
        table.setSpacingBefore(10f);
        table.setWidthPercentage(100);
        addTableHeader(table);

        List<Order> list = requestMap.getOrders();
        System.out.println("this is orders list : " + list);

        int no = 1;
        int totalAmount = 0;

        for (Order order : list) {
            addRows(table, order, no++);
            totalAmount += order.getTotal();
        }

        addTotalRow(table, totalAmount);

        document.add(table);

        Paragraph signature = new Paragraph(
                "Signature ");
        signature.setSpacingBefore(30f);
        signature.setAlignment(Element.ALIGN_RIGHT);
        document.add(signature);

        addBackgroundImage(writer);
        document.close();
    }

    // Add a last row with the total amount to the table
    private static void addTotalRow(PdfPTable table, double totalAmount) throws DocumentException {
        PdfPCell cell = new PdfPCell(new Phrase("Total", getFont("Data")));
        cell.setColspan(4); // Merge cells to cover Name, Qty, Price, and Total columns
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        table.addCell(Double.toString(totalAmount));
    }

    private static void addBackgroundImage(PdfWriter writer) throws DocumentException, IOException {
        // Get the PdfContentByte from the writer
        PdfContentByte content = writer.getDirectContentUnder();

        // Load the image
        Image image = Image.getInstance("backend\\src\\main\\java\\com\\cafe\\com\\cafe\\PDF\\img.jpg");

        image.setAbsolutePosition(0, 0);
        // Set opacity using PdfGState
        PdfGState gs = new PdfGState();
        gs.setFillOpacity(0.5f); // 0.0 is fully transparent, 1.0 is fully opaque

        // Set the graphic state to apply opacity
        content.setGState(gs);

        // Scale the image to fit the page size
        image.scaleAbsolute(writer.getPageSize());

        // Add the image to the background layer
        content.addImage(image);
    }

    // Inside addTableHeader method
    private static void addTableHeader(PdfPTable table) throws DocumentException {
        log.info("Inside addTableHeader");
        float[] columnWidths = { 2f, 10f, 2f, 2f, 2f };
        table.setWidths(columnWidths);

        // Set background color for the table
        table.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);

        Stream.of("Sr.No", "Name", "Qty", "Price", "Total").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBorderWidth(1);
            header.setPadding(8); // Add padding
            header.setPhrase(new Phrase(columnTitle, getFont("tableHeader")));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(header);
        });
    }

    private static void addRows(PdfPTable table, Order order, int no) {
        log.info("Inside addRows");

        table.getDefaultCell().setBackgroundColor(BaseColor.WHITE); // Set data cell background color

        table.addCell(String.valueOf(no));
        table.addCell(order.getProductName());
        table.addCell(order.getQuantity());
        table.addCell(order.getPrice());
        table.addCell(String.valueOf(order.getTotal()));
    }

    // this is method is used to get Font
    private static Font getFont(String type) {
        log.info("Inside getFont");
        switch (type) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
                // dataFont.setStyle(Font.BOLD);
                return dataFont;
            case "tableHeader":
                Font tableHeader = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.BLACK);
                // tableHeader.setStyle(Font.BOLD);
                return tableHeader;
            default:
                return new Font();
        }
    }

    // creates the margin around the pdf document
    private static void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectangleInPdf");
        Rectangle rectangle = new Rectangle(577, 825, 18, 15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);
    }
}
