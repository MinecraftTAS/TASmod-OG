package net.tasmod.renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;

// From the joal library, http://joal.dev.java.net/
import com.jogamp.openal.AL;

import paulscode.sound.Channel;
import paulscode.sound.SoundSystemConfig;

/**
 * The ChannelJOAL class is used to reserve a sound-card voice using the
 * JOAL binding of OpenAL.  Channels can be either normal or streaming
 * channels.
 *<b><br><br>
 *    This software is based on or using the JOAL Library available from
 *    http://joal.dev.java.net/
 *</b><br><br>
 *    JOAL License:
 *<br><i>
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 *<br>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *<br>
 * -Redistribution of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *<br>
 * -Redistribution in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *<br>
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * <br>
 * This software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS
 * LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT
 * OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *<br>
 * You acknowledge that this software is not designed or intended for use in the
 * design, construction, operation or maintenance of any nuclear facility.
 * <br><br><br></i>
 *<b><i>    SoundSystem LibraryJOAL License:</b></i><br><b><br>
 *<b>
 *    You are free to use this library for any purpose, commercial or otherwise.
 *    You may modify this library or source code, and distribute it any way you
 *    like, provided the following conditions are met:
 *<br>
 *    1) You must abide by the conditions of the aforementioned JOAL License.
 *<br>
 *    2) You may not falsely claim to be the author of this library or any
 *    unmodified portion of it.
 *<br>
 *    3) You may not copyright this library or a modified version of it and then
 *    sue me for copyright infringement.
 *<br>
 *    4) If you modify the source code, you must clearly document the changes
 *    made before redistributing the modified source code, so other users know
 *    it is not the original code.
 *<br>
 *    5) You are not required to give me credit for this library in any derived
 *    work, but if you do, you must also mention my website:
 *    http://www.paulscode.com
 *<br>
 *    6) I the author will not be responsible for any damages (physical,
 *    financial, or otherwise) caused by the use if this library or any part
 *    of it.
 *<br>
 *    7) I the author do not guarantee, warrant, or make any representations,
 *    either expressed or implied, regarding the use of this library or any
 *    part of it.
 * <br><br>
 *    Author: Paul Lamb
 * <br>
 *    http://www.paulscode.com
 * </b>
 */
public class ChannelJOAL extends Channel
{
/**
 * OpenAL's identifier for this channel.
 */    
    public int[] ALSource = null;
    
/**
 * OpenAL data format to use when playing back the assigned source.
 */    
    public int ALformat;	// OpenAL data format
    
/**
 * Sample rate (speed) to use for play-back.
 */    
    public int sampleRate;	// sample rate

/**
 * Miliseconds of buffers previously played (streaming sources).
 */
    public float millisPreviouslyPlayed = 0;

/**
 * Handle for accessing OpenAL.
 */
    private AL al = null;
    
/**
 * Constructor:  takes channelType identifier and a handle to the OpenAL 
 * identifier to use for this channel.  Possible values for channel type can be
 * found in the {@link paulscode.sound.SoundSystemConfig SoundSystemConfig}
 * class.
 * @param type Type of channel (normal or streaming).
 * @param src Handle to the OpenAL source identifier.
 */
    public ChannelJOAL( int type, int[] src )
    {
        super( type );
        libraryType = LibraryJOAL.class;
        ALSource = src;
        al = LibraryJOAL.getAL();
    }
    
/**
 * Empties the streamBuffers list, stops and deletes the ALSource, shuts the 
 * channel down, and removes references to all instantiated objects.
 */
    @Override
    public void cleanup()
    {
        if( ALSource != null )
        {
            try
            {
                // Stop playing the source:
                al.alSourceStop( ALSource[0] );
                al.alGetError();
            }
            catch( Exception e )
            {}
            try
            {
                // Delete the source:
                al.alDeleteSources( 1, ALSource, 0 );
                al.alGetError();
            }
            catch( Exception e )
            {}
        }
        ALSource = null;
        
        super.cleanup();
    }
    
/**
 * Attaches an OpenAL sound-buffer identifier for the sound data to be played 
 * back for a normal source.
 * @param buf Identifier for the sound data to play.
 * @return False if an error occurred.
 */
    public boolean attachBuffer( int[] buf )
    {
        // A sound buffer can only be attached to a normal source:
        if( errorCheck( channelType != SoundSystemConfig.TYPE_NORMAL,
                        "Sound buffers may only be attached to normal " +
                        "sources." ) )
            return false;
        
        // send the sound buffer to the channel:
        al.alSourcei( ALSource[0], AL.AL_BUFFER, buf[0] );
        
        // save the format for later, for determining milliseconds played
        if( attachedSource != null && attachedSource.rawDataFormat != null &&
            attachedSource.rawDataFormat != null )
            setAudioFormat( attachedSource.rawDataFormat );
        
        // Check for errors and return:
        return checkALError();
    }
    
/**
 * Sets the channel up to receive the specified audio format.
 * @param audioFormat Format to use when playing the stream data.
 */
    @Override
    public void setAudioFormat( AudioFormat audioFormat )
    {
        int soundFormat = 0;
        if( audioFormat.getChannels() == 1 )
        {
            if( audioFormat.getSampleSizeInBits() == 8 )
            {
                soundFormat = AL.AL_FORMAT_MONO8;
            }
            else if( audioFormat.getSampleSizeInBits() == 16 )
            {
                soundFormat = AL.AL_FORMAT_MONO16;
            }
            else
            {
                errorMessage( "Illegal sample size in method " +
                              "'setAudioFormat'" );
                return;
            }
        }
        else if( audioFormat.getChannels() == 2 )
        {
            if( audioFormat.getSampleSizeInBits() == 8 )
            {
                soundFormat = AL.AL_FORMAT_STEREO8;
            }
            else if( audioFormat.getSampleSizeInBits() == 16 )
            {
                soundFormat = AL.AL_FORMAT_STEREO16;
            }
            else
            {
                errorMessage( "Illegal sample size in method " +
                              "'setAudioFormat'" );
                return;
            }
        }
        else
        {
            errorMessage( "Audio data neither mono nor stereo in " +
                          "method 'setAudioFormat'" );
            return;
        }
        ALformat = soundFormat;
        sampleRate = (int) audioFormat.getSampleRate();
    }

/**
 * Sets the channel up to receive the specified OpenAL audio format and sample
 * rate.
 * @param format Format to use.
 * @param rate Sample rate (speed) to use.
 */
    public void setFormat( int format, int rate )
    {
        ALformat = format;
        sampleRate = rate;
    }
    
/**
 * Queues up the initial byte[] buffers of data to be streamed.
 * @param bufferList List of the first buffers to be played for a streaming source.
 * @return False if problem occurred or if end of stream was reached.
 */
    @Override
    public boolean preLoadBuffers( @SuppressWarnings("rawtypes") LinkedList bufferList )
    {
        // Stream buffers can only be queued for streaming sources:
        if( errorCheck( channelType != SoundSystemConfig.TYPE_STREAMING,
                        "Buffers may only be queued for streaming sources." ) )
            return false;
        
        if( errorCheck( bufferList == null,
                        "Buffer List null in method 'preLoadBuffers'" ) )
            return false;
        
        IntBuffer streamBuffers;
        
        // Remember if the channel was playing:
        boolean playing = playing();
        // stop the channel if it is playing:
        if( playing )
        {
            al.alSourceStop( ALSource[0] );
            checkALError();
        }
        int[] processed = new int[1];
        al.alGetSourcei( ALSource[0], AL.AL_BUFFERS_PROCESSED, processed, 0 );

        if( processed[0] > 0 )
        {
            streamBuffers = IntBuffer.wrap( new int[processed[0]] );
            al.alGenBuffers( processed[0], streamBuffers );
            if( errorCheck( checkALError(),
                 "Error clearing stream buffers in method 'preLoadBuffers'" ) )
                return false;
            al.alSourceUnqueueBuffers( ALSource[0], processed[0],
                                       streamBuffers );
            if( errorCheck( checkALError(),
                 "Error unqueuing stream buffers in method 'preLoadBuffers'" ) )
                return false;
        }
        // restart the channel if it was previously playing:
        if( playing )
        {
            al.alSourcePlay( ALSource[0] );
            checkALError();
        }
        
        streamBuffers = IntBuffer.wrap( new int[bufferList.size()] );
        al.alGenBuffers( bufferList.size(), streamBuffers );
        if( errorCheck( checkALError(),
             "Error generating stream buffers in method 'preLoadBuffers'" ) )
            return false;
        
        byte[] byteBuffer = null;
        for( int i = 0; i < bufferList.size(); i++ )
        {
            byteBuffer = (byte[]) bufferList.get(i);
            try
            {
                al.alBufferData( streamBuffers.get(i), ALformat,
                                 ByteBuffer.wrap( byteBuffer, 0,
                                                  byteBuffer.length ),
                                 byteBuffer.length,
                                 sampleRate );
            }
            catch( Exception e )
            {
                errorMessage( "Error creating buffers in method " +
                              "'preLoadBuffers'" );
                printStackTrace( e );
                return false;
            }
            if( errorCheck( checkALError(),
                         "Error creating buffers in method 'preLoadBuffers'" ) )
                return false;
        }
        
        try
        {
            al.alSourceQueueBuffers( ALSource[0], bufferList.size(),
                                     streamBuffers );
        }
        catch( Exception e )
        {
            errorMessage( "Error queuing buffers in method 'preLoadBuffers'" );
            printStackTrace( e );
            return false;
        }
        if( errorCheck( checkALError(),
                        "Error queuing buffers in method 'preLoadBuffers'" ) )
            return false;

        al.alSourcePlay( ALSource[0] );
        if( errorCheck( checkALError(),
                        "Error playing source in method 'preLoadBuffers'" ) )
            return false;
        
        // Success:
        return true;
    }
    
/**
 * Queues up a byte[] buffer of data to be streamed.
 * @param buffer The next buffer to be played for a streaming source.
 * @return False if an error occurred or if the channel is shutting down.
 */
    @Override
    public boolean queueBuffer( byte[] buffer )
    {
        // Stream buffers can only be queued for streaming sources:
        if( errorCheck( channelType != SoundSystemConfig.TYPE_STREAMING,
                        "Buffers may only be queued for streaming sources." ) )
            return false;
        
        IntBuffer intBuffer = IntBuffer.wrap( new int[1] );
        
        al.alSourceUnqueueBuffers( ALSource[0], 1, intBuffer );
        if( checkALError() )
            return false;
        

        if( al.alIsBuffer( intBuffer.get( 0 ) ) )
            millisPreviouslyPlayed += millisInBuffer( intBuffer.get( 0 ) );
        checkALError();

        al.alBufferData( intBuffer.get(0), ALformat,
                         ByteBuffer.wrap( buffer, 0, buffer.length ),
                         buffer.length, sampleRate );
        if( checkALError() )
            return false;
        
        al.alSourceQueueBuffers( ALSource[0], 1, intBuffer );
        if( checkALError() )
            return false;

        return true;
    }

/**
 * Feeds raw data to the stream.
 * @param buffer Buffer containing raw audio data to stream.
 * @return Number of prior buffers that have been processed., or -1 if error.
 */
    @Override
    public int feedRawAudioData( byte[] buffer )
    {
        // Stream buffers can only be queued for streaming sources:
        if( errorCheck( channelType != SoundSystemConfig.TYPE_STREAMING,
                      "Raw audio data can only be fed to streaming sources." ) )
            return -1;

        IntBuffer intBuffer;
        
        int[] processed = new int[1];
        al.alGetSourcei( ALSource[0], AL.AL_BUFFERS_PROCESSED, processed, 0 );

        if( processed[0] > 0 )
        {
            intBuffer = IntBuffer.wrap( new int[processed[0]] );
            al.alGenBuffers( processed[0], intBuffer );
            if( errorCheck( checkALError(),
                "Error clearing stream buffers in method 'feedRawAudioData'" ) )
                return -1;
            al.alSourceUnqueueBuffers( ALSource[0], processed[0],
                                       intBuffer );
            if( errorCheck( checkALError(),
               "Error unqueuing stream buffers in method 'feedRawAudioData'" ) )
                return -1;

            int i;
            intBuffer.rewind();
            while( intBuffer.hasRemaining() )
            {
                i = intBuffer.get();
                if( al.alIsBuffer( i ) )
                {
                    millisPreviouslyPlayed += millisInBuffer( i );
                }
                checkALError();
            }
            al.alDeleteBuffers( processed[0], intBuffer );
            checkALError();
        }
        intBuffer = IntBuffer.wrap( new int[1] );
        al.alGenBuffers( 1, intBuffer );
        if( errorCheck( checkALError(),
                        "Error generating stream buffers in method 'preLoadBuffers'" ) )
            return -1;

        al.alBufferData( intBuffer.get(0), ALformat,
                         ByteBuffer.wrap( buffer, 0, buffer.length ),
                         buffer.length, sampleRate );
        if( checkALError() )
            return -1;

        al.alSourceQueueBuffers( ALSource[0], 1, intBuffer );
        if( checkALError() )
            return -1;

        if( attachedSource != null && attachedSource.channel == this &&
            attachedSource.active() )
        {
            // restart the channel if it was previously playing:
            if( !playing() )
            {
                al.alSourcePlay( ALSource[0] );
                checkALError();
            }
        }

        return processed[0];
    }
    
/**
 * Returns the number of milliseconds of audio contained in specified buffer.
 * @return milliseconds, or 0 if unable to calculate.
 */
    public float millisInBuffer( int alBufferi )
    {
        int size[] = new int[1];
        int channels[] = new int[1];
        int bits[] = new int[1];
        al.alGetBufferi( alBufferi, AL.AL_SIZE, size, 0 );
        checkALError();
        al.alGetBufferi( alBufferi, AL.AL_CHANNELS, channels, 0 );
        checkALError();
        al.alGetBufferi( alBufferi, AL.AL_BITS, bits, 0 );
        checkALError();

        return( ( (float) size[0] / (float) channels[0] / ( (float) bits[0] /
                                         8.0f ) / (float) sampleRate ) * 1000 );
    }
    
/**
 * Calculates the number of milliseconds since the channel began playing.
 * @return Milliseconds, or -1 if unable to calculate.
 */
    public float getMillisPreviouslyPlayed()
    {
        int byteOffset[] = new int[1];
        // get number of samples played in current buffer
        al.alGetSourcei( ALSource[0], AL.AL_BYTE_OFFSET, byteOffset, 0 );

        float bytesPerFrame = 1f;
        switch( ALformat )
        {
            case AL.AL_FORMAT_MONO8 :
                bytesPerFrame = 1f;
                break;
            case AL.AL_FORMAT_MONO16 :
                bytesPerFrame = 2f;
                break;
            case AL.AL_FORMAT_STEREO8 :
                bytesPerFrame = 2f;
                break;
            case AL.AL_FORMAT_STEREO16 :
                bytesPerFrame = 4f;
                break;
            default :
                break;
        }

        float offset = ( ( (float) byteOffset[0] / bytesPerFrame ) /
                                                    (float) sampleRate ) * 1000;

        // add the milliseconds from stream-buffers that played previously
        if( channelType == SoundSystemConfig.TYPE_STREAMING )
            offset += millisPreviouslyPlayed;

        // Return millis played:
        return( offset );
    }
/**
 * Returns the number of queued byte[] buffers that have finished playing.
 * @return Number of buffers processed.
 */
    @Override
    public int buffersProcessed()
    {
        // Only streaming sources process buffers:
        if( channelType != SoundSystemConfig.TYPE_STREAMING )
            return 0;
        
        // determine how many have been processed:
        int[] processed = new int[1];
        al.alGetSourcei( ALSource[0], AL.AL_BUFFERS_PROCESSED, processed, 0 );
        
        // Check for errors:
        if( checkALError() )
            return 0;
        
        // Return how many were processed:
        return processed[0];
    }
    
/**
 * Dequeues all previously queued data.
 */
    @Override
    public void flush()
    {
        // Only a streaming source can be flushed, because only streaming
        // sources have queued buffers:
        if( channelType != SoundSystemConfig.TYPE_STREAMING )
            return;
        
        // determine how many buffers have been queued:
        int[] queued = new int[1];
        al.alGetSourcei( ALSource[0], AL.AL_BUFFERS_QUEUED, queued, 0 );

        // Check for errors:
        if( checkALError() )
            return;
        
        IntBuffer intBuffer = IntBuffer.wrap( new int[1] );
        while( queued[0] > 0 )
        {
            try
            {
                al.alSourceUnqueueBuffers( ALSource[0], 1, intBuffer );
            }
            catch( Exception e )
            {
                return;
            }
            if( checkALError() )
                return;
            queued[0]--;
        }
       	millisPreviouslyPlayed = 0;
    }
    
/**
 * Stops the channel, dequeues any queued data, and closes the channel.
 */
    @Override
    public void close()
    {
        try
        {
            al.alSourceStop( ALSource[0] );
            al.alGetError();
        }
        catch( Exception e )
        {}
        
        if( channelType == SoundSystemConfig.TYPE_STREAMING )
            flush();
    }
    
/**
 * Plays the currently attached normal source, opens this channel up for 
 * streaming, or resumes playback if this channel was paused.
 */
    @Override
    public void play()
    {
        al.alSourcePlay( ALSource[0] );
        checkALError();
    }
    
/**
 * Temporarily stops playback for this channel.
 */
    @Override
    public void pause()
    {
        al.alSourcePause( ALSource[0] );
        checkALError();
    }
    
/**
 * Stops playback for this channel and rewinds the attached source to the 
 * beginning.
 */
    @Override
    public void stop()
    {
        al.alSourceStop( ALSource[0] );
        if( !checkALError() )
            millisPreviouslyPlayed = 0;
    }
    
/**
 * Rewinds the attached source to the beginning.  Stops the source if it was 
 * paused.
 */
    @Override
    public void rewind()
    {
        // rewinding for streaming sources is handled elsewhere
        if( channelType == SoundSystemConfig.TYPE_STREAMING )
            return;
        
        al.alSourceRewind( ALSource[0] );
        if( !checkALError() )
            millisPreviouslyPlayed = 0;
    }
    
    
/**
 * Used to determine if a channel is actively playing a source.  This method 
 * will return false if the channel is paused or stopped and when no data is 
 * queued to be streamed.
 * @return True if this channel is playing a source.
 */
    @Override
    public boolean playing()
    {
        int[] state = new int[1];
        al.alGetSourcei( ALSource[0], AL.AL_SOURCE_STATE, state, 0 );
        if( checkALError() )
            return false;
        
        return( state[0] == AL.AL_PLAYING );
    }
    
/**
 * Checks for OpenAL errors, and prints a message if there is an error.
 * @return True if there was an error, False if not.
 */
    private boolean checkALError()
    {
        switch( al.alGetError() )
        {
            case AL.AL_NO_ERROR:
                return false;
            case AL.AL_INVALID_NAME:
                errorMessage( "Invalid name parameter." );
                return true;
            case AL.AL_INVALID_ENUM:
                errorMessage( "Invalid parameter." );
                return true;
            case AL.AL_INVALID_VALUE:
                errorMessage( "Invalid enumerated parameter value." );
                return true;
            case AL.AL_INVALID_OPERATION:
                errorMessage( "Illegal call." );
                return true;
            case AL.AL_OUT_OF_MEMORY:
                errorMessage( "Unable to allocate memory." );
                return true;
            default:
                errorMessage( "An unrecognized error occurred." );
                return true;
        }
    }
}
