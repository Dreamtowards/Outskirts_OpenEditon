package outskirts.client.audio;

import static org.lwjgl.openal.AL10.*;

public class AudioSource {

    private final int sourceID;

    public AudioSource() {
        this.sourceID = alGenSources();

        alSourcef(sourceID, AL_ROLLOFF_FACTOR, 0.9f);
        alSourcef(sourceID, AL_REFERENCE_DISTANCE, 3);
    }

    public int sourceID() {
        return sourceID;
    }

    public void play() {
        alSourcePlay(sourceID);
    }
    public void pause() {
        alSourcePause(sourceID);
    }
    public void stop() {
        alSourceStop(sourceID);
    }


    public void setLooping(boolean looping) {
        alSourcei(sourceID, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
    }

    private int sourceState() {
        return alGetSourcei(sourceID, AL_SOURCE_STATE);
    }
    public boolean isPlaying() {
        return sourceState() == AL_PLAYING;
    }
    public boolean isPaused() {
        return sourceState() == AL_PAUSED;
    }
    public boolean isStopped() {
        return sourceState() == AL_STOPPED;
    }


    public void setVolume(float volume) {
        alSourcef(sourceID, AL_GAIN, volume);
    }
    public float getVolume() {
        return alGetSourcef(sourceID, AL_GAIN);
    }

    public void setPitch(float pitch) {
        alSourcef(sourceID, AL_PITCH, pitch);
    }
    public float getPitch() {
        return alGetSourcef(sourceID, AL_PITCH);
    }

    public void setPosition(float x, float y, float z) {
        alSource3f(sourceID, AL_POSITION, x, y, z);
    }

    public void setVelocity(float x, float y, float z) {
        alSource3f(sourceID, AL_VELOCITY, x, y, z);
    }




    public void queneBuffers(int bufferID) {
        alSourceQueueBuffers(sourceID, bufferID);
    }
    public int unqueueBuffers() {
        return alSourceUnqueueBuffers(sourceID);
    }

    public int buffersProcessed() {
        return alGetSourcei(sourceID, AL_BUFFERS_PROCESSED);
    }
    public int buffersQueued() {
        return alGetSourcei(sourceID, AL_BUFFERS_QUEUED);
    }


    public void delete() {
        stop();
        alDeleteSources(sourceID);
    }
}
