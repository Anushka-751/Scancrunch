package com.scanCrunch.core.util;

import org.springframework.stereotype.Component;

import com.scanCrunch.domain.receipt.payload.ReceiptItemPayload;
import com.scanCrunch.domain.receipt.payload.ReceiptPayload;

/**
 * Pure HTML builder — given a fully formatted {@link ReceiptPayload}, returns
 * the finished HTML email body. Contains no data-fetching or business logic.
 */
@Component
public class HtmlReceiptGenerator {

    public String generate(ReceiptPayload payload) {

        StringBuilder itemsHtml = new StringBuilder();

        int index = 1;

        for (ReceiptItemPayload item : payload.getItems()) {

            itemsHtml.append("<tr>")
                    .append("<td style=\"padding:8px 0;color:#333;\">")
                    .append(index++).append(". ").append(escape(item.getFoodName()))
                    .append(" x").append(item.getQuantity())
                    .append("</td>")
                    .append("<td style=\"padding:8px 0;text-align:right;color:#333;\">")
                    .append(escape(item.getFormattedSubtotal()))
                    .append("</td>")
                    .append("</tr>");
        }

        return "<!DOCTYPE html>"
                + "<html>"
                + "<body style=\"margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,Helvetica,sans-serif;\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">"
                + "<tr><td align=\"center\" style=\"padding:24px 0;\">"
                + "<table role=\"presentation\" width=\"480\" cellpadding=\"0\" cellspacing=\"0\" "
                + "style=\"background:#ffffff;border-radius:8px;overflow:hidden;\">"
                + "<tr><td style=\"background:#ff5a1f;padding:20px;text-align:center;\">"
                + "<h1 style=\"margin:0;color:#ffffff;font-size:22px;\">" + escape(payload.getRestaurantName()) + "</h1>"
                + "</td></tr>"
                + "<tr><td style=\"padding:24px;\">"
                + "<h2 style=\"margin:0 0 12px 0;color:#2e7d32;\">Payment Successful</h2>"
                + "<p style=\"margin:0 0 16px 0;color:#333;\">Hello " + escape(payload.getCustomerName()) + ","
                + "<br/>Thank you for ordering with " + escape(payload.getRestaurantName()) + ".</p>"
                + "<table role=\"presentation\" width=\"100%\" style=\"margin-bottom:16px;font-size:14px;color:#333;\">"
                + "<tr><td>Order ID</td><td align=\"right\">" + escape(payload.getOrderId()) + "</td></tr>"
                + "<tr><td>Payment ID</td><td align=\"right\">" + escape(payload.getPaymentId()) + "</td></tr>"
                + "</table>"
                + "<h3 style=\"margin:0 0 8px 0;color:#333;border-bottom:1px solid #eee;padding-bottom:8px;\">Items</h3>"
                + "<table role=\"presentation\" width=\"100%\" style=\"font-size:14px;\">"
                + itemsHtml
                + "</table>"
                + "<hr style=\"border:none;border-top:1px solid #eee;margin:16px 0;\"/>"
                + "<table role=\"presentation\" width=\"100%\" style=\"font-size:15px;color:#333;\">"
                + "<tr><td><strong>Total Amount</strong></td><td align=\"right\"><strong>"
                + escape(payload.getFormattedTotalAmount()) + "</strong></td></tr>"
                + "<tr><td>Payment Method</td><td align=\"right\">" + escape(payload.getPaymentMethod()) + "</td></tr>"
                + "<tr><td>Payment Status</td><td align=\"right\" style=\"color:#2e7d32;font-weight:bold;\">"
                + escape(payload.getPaymentStatus()) + "</td></tr>"
                + "<tr><td>Date</td><td align=\"right\">" + escape(payload.getPaymentDate()) + "</td></tr>"
                + "</table>"
                + "<p style=\"margin-top:24px;color:#777;font-size:13px;text-align:center;\">"
                + "Thank you for choosing " + escape(payload.getRestaurantName()) + ".</p>"
                + "</td></tr>"
                + "</table>"
                + "</td></tr>"
                + "</table>"
                + "</body>"
                + "</html>";
    }

    private String escape(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
