package com.example.minierp.application.common.utils;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

/**
 * A utility class for creating PDF documents with Farsi (RTL) content.
 * It centralizes font loading, cell creation, and other common PDF tasks.
 */
public final class PdfUtils {

    // --- SHARED FONTS AND FORMATTERS ---
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

    public static final Font FARSI_NORMAL_FONT;
    public static final Font FARSI_BOLD_FONT;
    public static final Font FARSI_TITLE_FONT;
    public static final Font FARSI_HEADER_FONT;

    // Static initializer block to load fonts once
    static {
        Font normal, bold, title, header;
        try {
            BaseFont farsiBaseFont = BaseFont.createFont("fonts/Vazir.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            normal = new Font(farsiBaseFont, 10, Font.NORMAL);
            bold = new Font(farsiBaseFont, 12, Font.BOLD);
            title = new Font(farsiBaseFont, 18, Font.BOLD);
            header = new Font(farsiBaseFont, 11, Font.BOLD);
        } catch (DocumentException | IOException e) {
            System.err.println("FATAL: Could not load Farsi font! Using fallback. " + e.getMessage());
            normal = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
            title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
            header = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.BOLD);
        }
        FARSI_NORMAL_FONT = normal;
        FARSI_BOLD_FONT = bold;
        FARSI_TITLE_FONT = title;
        FARSI_HEADER_FONT = header;
    }

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PdfUtils() {}


    // --- HELPER METHODS ---

    /**
     * Creates a standard PdfPCell with proper RTL settings for Farsi text.
     */
    public static PdfPCell createFarsiCell(String text, Font font) {
        Phrase phrase = new Phrase(text, font);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5f);
        return cell;
    }

    /**
     * Creates a borderless, full-width PdfPTable to reliably render a single line of Farsi text.
     */
    public static PdfPTable createFarsiParagraphTable(String text, Font font, int alignment) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell cell = createFarsiCell(text, font);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
        return table;
    }

    /**
     * Adds a styled header row to the given table.
     */
    public static void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = createFarsiCell(header, FARSI_HEADER_FONT);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new java.awt.Color(230, 230, 230));
            table.addCell(cell);
        }
    }

    /**
     * Converts a Java LocalDateTime to a Solar (Jalali) date string in YYYY/MM/DD format.
     */
    public static String toSolarDate(LocalDateTime gregorianDateTime) {
        if (gregorianDateTime == null) { return ""; }
        Date gregorianDate = Date.from(gregorianDateTime.atZone(ZoneId.systemDefault()).toInstant());
        ULocale locale = new ULocale("fa_IR@calendar=persian");
        Calendar persianCalendar = Calendar.getInstance(locale);
        persianCalendar.setTime(gregorianDate);
        int year = persianCalendar.get(Calendar.YEAR);
        int month = persianCalendar.get(Calendar.MONTH) + 1;
        int day = persianCalendar.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.US, "%d/%02d/%02d", year, month, day);
    }

    /**
     * Formats a BigDecimal value into a string with US locale formatting (e.g., with commas).
     */
    public static String formatAmount(BigDecimal amount) {
        return NUMBER_FORMAT.format(amount);
    }
}