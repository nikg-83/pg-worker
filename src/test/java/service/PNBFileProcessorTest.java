package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.paymentgateway.service.PNBFileProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PNBFileProcessorTest {

    public static void main (String args[]) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = "[{\"TxnNo\":\"S74697456\",\"TxnDate\":\"29-04-2023\",\"Description\":\"ATM\\/311959965104\\/P2V\\/lovamarrisetti@oksbi\\/MARISETT\",\"BranchName\":\"-\",\"DrAmount\":null,\"CrAmount\":\"2,000.00\",\"Balance\":\"11,99,052.20 Cr.\",\"KimsRemar\":null},{\"TxnNo\":\"S74687048\",\"TxnDate\":\"29-04-2023\",\"Description\":\"UPI\\/311959939681\\/P2V\\/niranjanenriques220@oksbi\\/NIR\",\"BranchName\":\"-\",\"DrAmount\":null,\"CrAmount\":\"5,600.00\",\"Balance\":\"11,97,052.20 Cr.\",\"KimsRemar\":null}]";
            System.out.println(jsonString);
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            Pattern pattern = Pattern.compile("UPI/(\\w+)/(\\w+)/(\\w+)/(.+)/");
            for (JsonNode row : jsonNode) {
                String description = row.get("Description").toString();

                if (StringUtils.hasLength(description)) {
                    System.out.println(description);
                    Matcher matcher = pattern.matcher(description);
                    if (matcher.find()) {
                        System.out.println("UPI Id -" + matcher.group(1));
                        System.out.println("Txn No - " + row.get("TxnNo"));
                        System.out.println("Cr Amount - " + row.get("CrAmount"));
                        System.out.println("Txn Date - " + row.get("TxnDate"));
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
