package org.jnbis.internal.record.reader;

import org.jnbis.internal.NistHelper;
import org.jnbis.api.model.record.TransactionInformation;
import org.jnbis.api.model.record.TransactionInformation.InfoDesignation;
import org.jnbis.api.model.record.TransactionInformation.TransactionContent;

/**
 * @author ericdsoto
 */
public class TransactionInfoReader extends RecordReader {

    @Override
    public TransactionInformation read(NistHelper.Token token) {
        if (token.pos >= token.buffer.length) {
            throw new RuntimeException("T1::NULL pointer to T1 record");
        }

        TransactionInformation transaction = new TransactionInformation();

        while (true) {
            NistHelper.Tag tag = getTagInfo(token);

            if (tag.type != NistHelper.RT_TRANSACTION_INFO) {
                throw new RuntimeException("T1::Invalid Record Type : " + tag.type);
            }

            String value = nextWord(token, NistHelper.TAG_SEP_GSFS, NistHelper.FIELD_MAX_LENGTH - 1, false);

            switch (tag.field) {
            case 1:
                transaction.setLogicalRecordLength(value);
                break;
            case 2:
                transaction.setVersion(value);
                break;
            case 3:
                token.header = value;
                NistHelper.Token contentToken = new NistHelper.Token(value.getBytes());
                
                TransactionContent content = new TransactionContent();
                String subField = nextWord(contentToken, NistHelper.TAG_SEP_USRS, NistHelper.FIELD_MAX_LENGTH - 1, false);
                content.setFirstRecordCategoryCode(Integer.parseInt(subField));
                contentToken.pos++;
                subField = nextWord(contentToken, NistHelper.TAG_SEP_USRS, NistHelper.FIELD_MAX_LENGTH - 1, false);
                content.setContentRecordCount(Integer.parseInt(subField));
                contentToken.pos++;

                while (true) {
                    subField = nextWord(contentToken, NistHelper.TAG_SEP_USRS, NistHelper.FIELD_MAX_LENGTH - 1, false);
                    int recordType = Integer.parseInt(subField);
                    contentToken.pos++;
                    subField = nextWord(contentToken, NistHelper.TAG_SEP_USRS, NistHelper.FIELD_MAX_LENGTH - 1, false);
                    int idc = Integer.parseInt(subField);

                    content.getIdcs().add(new InfoDesignation(recordType, idc));
                    
                    if (contentToken.pos++ == contentToken.buffer.length || contentToken.buffer[contentToken.pos] == NistHelper.SEP_GS) {
                        break;
                    }
                }
                transaction.setTransactionContent(content);
                break;
            case 4:
                transaction.setTypeOfTransaction(value);
                break;
            case 5:
                transaction.setDate(value);
                break;
            case 6:
                transaction.setPriority(value);
                break;
            case 7:
                transaction.setDestinationAgencyId(value);
                break;
            case 8:
                transaction.setOriginatingAgencyId(value);
                break;
            case 9:
                transaction.setControlNumber(value);
                break;
            case 10:
                transaction.setControlReferenceNumber(value);
                break;
            case 11:
                transaction.setNativeScanningResolution(value);
                break;
            case 12:
                transaction.setNominalTransmittingResolution(value);
                break;
            case 13:
                transaction.setDomainName(value);
                break;
            case 14:
                transaction.setGreenwichMeanTime(value);
                break;
            case 15:
                token.setCharSetDecoder(value);
                transaction.setDirectoryOfCharsets(value);
                break;
            }

            if (token.buffer[token.pos++] == NistHelper.SEP_FS) {
                break;
            }
        }

        return transaction;
    }
}
