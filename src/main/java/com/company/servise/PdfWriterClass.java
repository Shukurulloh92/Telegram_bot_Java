package com.company.servise;

import com.company.telegram.Bot;
import com.company.telegram.SQLServiseBot;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import static com.company.telegram.Bot.users;

public class PdfWriterClass {


    public static void exporTestResult() {

        String result = SQLServiseBot.countBall();
        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance
                    (document, new FileOutputStream(users.getCurrentSubject() + ".pdf"));
            document.open();
            document.add(new Paragraph(result));
            document.close();
            writer.close();

            Bot bot = new Bot();

            SendDocument sendDocument = new SendDocument(String.valueOf(users.getUserId()),
                    new InputFile()
                            .setMedia(new File("C:\\Users/HP/IdeaProjects/It_logic_test_bot/" + users.getCurrentSubject() + ".pdf")));
            bot.sendMsg(sendDocument);

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static boolean exportHistory() {

        String result = SQLServiseBot.usersResponseHistory();

        if (result != null && result.endsWith("a list of all your test solutions")) {
            return false;
        }

        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance
                    (document, new FileOutputStream(users.getFirstName() + "`s results .pdf"));
            document.open();
            document.add(new Paragraph(result));
            document.close();
            writer.close();

            Bot bot = new Bot();

            SendDocument sendDocument = new SendDocument(String.valueOf(users.getUserId()),
                    new InputFile().setMedia(new File(
                            "C:\\Users/HP/IdeaProjects/It_logic_test_bot/" + users.getFirstName() + "`s results .pdf")));
            bot.sendMsg(sendDocument);

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }


    public static void awardWithCertificate(long certicateId) {
        String currentDay = PdfWriterClass.currentDay();

        String name = users.getFirstName();
        if (users.getLastName() != null) {
            name += " " + users.getLastName();
        }
        if (name.length() > 25) {
            name = users.getFirstName();
        }

        Font fontName = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 34,
                new CMYKColor(0, 0, 0, 255));
        Font fontText = FontFactory.getFont(FontFactory.HELVETICA, 15,
                new CMYKColor(0, 0, 0, 225));
        Font fontDate = FontFactory.getFont(FontFactory.HELVETICA, 17,
                new CMYKColor(0, 0, 0, 225));

        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter writer = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get("Certificate to " + name + ".pdf")));
            document.open();

            Image image1 = Image.getInstance("Logo.jpg");
            image1.setAbsolutePosition(0f, 0f);
            image1.scaleAbsolute(842, 595);
            document.add(image1);


            PdfPTable table = new PdfPTable(1); // 1 columns.
            table.setWidthPercentage(1);

            table.setSpacingBefore(10);
            table.setSpacingAfter(33f);

            PdfPCell cell1 = new PdfPCell(new Paragraph(""));
            cell1.setBorderColor(BaseColor.WHITE);
            cell1.setBorder(0);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);


            PdfPTable table2 = new PdfPTable(1); // 1 columns.
            table2.setWidthPercentage(80);
            table2.setSpacingBefore(250);
            table2.setSpacingAfter(-13f);

            PdfPCell cell2 = new PdfPCell(new Phrase(name, fontName));
            cell2.setBorderColor(BaseColor.RED);
            cell2.setPadding(18);
            cell2.setBorder(0);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);


            PdfPTable table3 = new PdfPTable(1); // 1 columns.
            table3.setWidthPercentage(80);
            table3.setSpacingAfter(10f);

            PdfPCell cell3 = new PdfPCell(new Phrase("This certificate is awarded to " + name +
                    " for successfully passing the " + users.getCurrentSubject() + " Exam conducted " +
                    "online by It Logic Academy. The Reference Id for this certificate on " +
                    "t.me/it_logic_testsbot is " + certicateId + ".", fontText));
            cell3.setBorderColor(BaseColor.RED);
            cell3.setPadding(18);
            cell3.setBorder(0);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);


            PdfPTable table4 = new PdfPTable(1); // 1 columns.
            table4.setWidthPercentage(50);
            table4.setSpacingBefore(25);
            table4.setSpacingAfter(10);

            PdfPCell cell4 = new PdfPCell(new Phrase(currentDay, fontDate));
            cell4.setBorderColor(BaseColor.RED);
            cell4.setPadding(5);
            cell4.setBorder(0);
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);


            table.addCell(cell1);
            table2.addCell(cell2);
            table3.addCell(cell3);
            table4.addCell(cell4);

            document.add(table);
            document.add(table2);
            document.add(table3);
            document.add(table4);


            document.close();
            writer.close();

            Bot bot = new Bot();

            SendDocument sendDocument2 = new SendDocument(String.valueOf(users.getUserId()),
                    new InputFile()
                            .setMedia(new File("C:\\Users/HP/IdeaProjects/It_logic_test_bot/Certificate to " + name + ".pdf")));
            bot.sendMsg(sendDocument2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String currentDay() {
        Date date1 = new Date();
        return new SimpleDateFormat("dd/MM/yyyy").format(date1);
    }


    public static Integer addNewcerticateId() {
        ArrayList<Integer> certificateIds = new ArrayList<>(Objects.requireNonNull(SQLServiseBot.getCertificateIds()));
        long max = 899998;
        long min = 100001;
        int len = certificateIds.size();
        certificateIds.add(((int) (Math.random() * max + min)));
        for (int i = 0; i < len; i++) {
            if (certificateIds.get(len).equals(certificateIds.get(i))) {
                certificateIds.set(len, (int) (Math.random() * max + min));
                i = -1;
            }
        }
        return certificateIds.get(certificateIds.size() - 1);
    }


    private static String sms() {
        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        int length = 5;
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphaNumeric.length());
            char randomChar = alphaNumeric.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
