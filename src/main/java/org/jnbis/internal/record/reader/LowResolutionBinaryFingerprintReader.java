package org.jnbis.internal.record.reader;

import org.jnbis.internal.NistHelper;
import org.jnbis.api.model.record.LowResolutionBinaryFingerprint;

/**
 * @author ericdsoto
 */
public class LowResolutionBinaryFingerprintReader extends RecordReader {

    @Override
    public LowResolutionBinaryFingerprint read(NistHelper.Token token) {
        if (token.pos >= token.buffer.length) {
            throw new RuntimeException("T3::NULL pointer to T3 record");
        }

        LowResolutionBinaryFingerprint fingerprint = new LowResolutionBinaryFingerprint();

        //Assigning t3-Header values
        Integer length = (int) readInt(token);
        int fingerPrintNo = token.buffer[token.pos + 6];

        int dataSize = length - 18;

        if (token.pos + dataSize + 17 > token.buffer.length) {
            dataSize += token.buffer.length - token.pos - 18;
        }

        byte[] data = new byte[dataSize];
        System.arraycopy(token.buffer, token.pos + 18, data, 0, data.length + 18 - 18);

        token.pos += length;
        fingerprint.setIdc(fingerPrintNo);
        fingerprint.setImageData(data);
        fingerprint.setLogicalRecordLength(length.toString());

        return fingerprint;
    }
}
