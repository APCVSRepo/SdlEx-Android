package demo.sdlex.test;

import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThru;
import com.smartdevicelink.proxy.rpc.TTSChunk;
import com.smartdevicelink.proxy.rpc.enums.AudioType;
import com.smartdevicelink.proxy.rpc.enums.BitsPerSample;
import com.smartdevicelink.proxy.rpc.enums.SamplingRate;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Vector;

public class AudioRecorder
{
    private static final int CHANNELS = 1;
    private static final int SAMPLE_RATE = 16000;
    private static final int BITS_PER_SAMPLE = 16;
    private ByteBuffer mAptByteBuf = ByteBuffer.allocate(1024 * 1024 * 4);
    private int mAptByteCount = 0;



    public PerformAudioPassThru buildAPT(String tts, String displayText1, String displayText2, int duration, int correlationID)
    {
        PerformAudioPassThru msgAPT = new PerformAudioPassThru();
        Vector<TTSChunk> initChunks = TTSChunkFactory.createSimpleTTSChunks(tts);
        msgAPT.setInitialPrompt(initChunks);
        msgAPT.setAudioPassThruDisplayText1(displayText1);
        msgAPT.setAudioPassThruDisplayText2(displayText2);
        msgAPT.setSamplingRate(SamplingRate._16KHZ);
        msgAPT.setMaxDuration(duration);
        msgAPT.setBitsPerSample(BitsPerSample._16_BIT);
        msgAPT.setAudioType(AudioType.PCM);
        msgAPT.setCorrelationID(correlationID);
        msgAPT.setMuteAudio(true);

        return msgAPT;
    }


    public void stop()
    {
        byte[] wav = Pcm2WavUtil.pcmToWav(mAptByteBuf.array(), mAptByteCount, CHANNELS, SAMPLE_RATE, BITS_PER_SAMPLE);
        mAptByteBuf.clear();
        mAptByteCount = 0;

        try
        {
            // convert to WAV
            DataOutputStream dos = new DataOutputStream(new FileOutputStream
                                                                (new File("/sdcard", "test.wav")));
            dos.write(wav, 0, wav.length);
            dos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void record(byte[] data)
    {
        mAptByteBuf.put(data);
        mAptByteCount += data.length;
    }

}
