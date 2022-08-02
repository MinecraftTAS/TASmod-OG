package net.tasmod.renderer;

import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;

// From the joal library, http://joal.dev.java.net/
import com.jogamp.openal.AL;

import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.Source;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Vector3D;

/**
 * The SourceJOAL class provides an interface to the JOAL binding of OpenAL.
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
public class SourceJOAL extends Source
{
/**
 * The source's basic Channel type-cast to a ChannelJOAL.
 */
    private ChannelJOAL channelOpenAL = (ChannelJOAL) channel;
    
/**
 * OpenAL sound-buffer identifier for this source if it is a normal source.  
 */
    private int[] myBuffer;
    
/**
 * Vector3D containing the listener's 3D coordinates.
 */
    private Vector3D listenerPosition;

/**
 * Handle for accessing OpenAL.
 */
    private AL al = null;

/**
 * Constructor:  Creates a new source using the specified parameters.
 * @param listenerPosition Handle to the Vector3D containing the listener's 3D coordinates.
 * @param myBuffer OpenAL sound-buffer identifier to use for a new normal source.
 * @param priority Setting this to true will prevent other sounds from overriding this one.
 * @param toStream Setting this to true will create a streaming source.
 * @param toLoop Should this source loop, or play only once.
 * @param sourcename A unique identifier for this source.  Two sources may not use the same sourcename.
 * @param filenameURL Filename/URL of the sound file to play at this source.
 * @param soundBuffer Buffer containing audio data, or null if not loaded yet.
 * @param x X position for this source.
 * @param y Y position for this source.
 * @param z Z position for this source.
 * @param attModel Attenuation model to use.
 * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of 'att'.
 * @param temporary Whether or not to remove this source after it finishes playing.
 */
    public SourceJOAL( Vector3D listenerPosition, int[] myBuffer,
                       boolean priority, boolean toStream, boolean toLoop,
                       String sourcename, FilenameURL filenameURL,
                       SoundBuffer soundBuffer, float x, float y, float z,
                       int attModel, float distOrRoll, boolean temporary )
    {
        super( priority, toStream, toLoop, sourcename, filenameURL, soundBuffer,
               x, y, z, attModel, distOrRoll, temporary );
        if( codec != null )
            codec.reverseByteOrder( true );
        this.listenerPosition = listenerPosition;
        this.myBuffer = myBuffer;
        libraryType = LibraryJOAL.class;
        pitch = 1.0f;
        al = LibraryJOAL.getAL();
        positionChanged();
    }
    
/**
 * Constructor:  Creates a new source matching the specified source.
 * @param listenerPosition Handle to the Vector3D containing the listener's 3D coordinates.
 * @param myBuffer OpenAL sound-buffer identifier to use for a new normal source.
 * @param old Source to copy information from.
 * @param soundBuffer Buffer containing audio data, or null if not loaded yet.
 */
    public SourceJOAL( Vector3D listenerPosition, int[] myBuffer, Source old,
                       SoundBuffer soundBuffer )
    {
        super( old, soundBuffer );
        if( codec != null )
            codec.reverseByteOrder( true );
        this.listenerPosition = listenerPosition;
        this.myBuffer = myBuffer;
        libraryType = LibraryJOAL.class;
        pitch = 1.0f;
        al = LibraryJOAL.getAL();
        positionChanged();
    }

/**
 * Constructor:  Creates a new streaming source that will be directly fed with
 * raw audio data.
 * @param listenerPosition Handle to the Vector3D containing the listener's 3D coordinates.
 * @param audioFormat Format that the data will be in.
 * @param priority Setting this to true will prevent other sounds from overriding this one.
 * @param sourcename A unique identifier for this source.  Two sources may not use the same sourcename.
 * @param x X position for this source.
 * @param y Y position for this source.
 * @param z Z position for this source.
 * @param attModel Attenuation model to use.
 * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of 'att'.
 */
    public SourceJOAL( Vector3D listenerPosition, AudioFormat audioFormat,
                       boolean priority, String sourcename, float x,
                       float y, float z, int attModel, float distOrRoll )
    {
        super( audioFormat, priority, sourcename, x, y, z, attModel,
               distOrRoll );
        this.listenerPosition = listenerPosition;
        libraryType = LibraryJOAL.class;
        pitch = 1.0f;
        al = LibraryJOAL.getAL();
        positionChanged();
    }
    
/**
 * Shuts the source down and removes references to all instantiated objects.
 */
    @Override
    public void cleanup()
    {
        
        super.cleanup();
    }
    
/**
 * Changes the peripheral information about the source using the specified 
 * parameters.
 * @param listenerPosition Handle to the Vector3D containing the listener's 3D coordinates.
 * @param myBuffer OpenAL sound-buffer identifier to use for a new normal source.
 * @param priority Setting this to true will prevent other sounds from overriding this one.
 * @param toStream Setting this to true will create a streaming source.
 * @param toLoop Should this source loop, or play only once.
 * @param sourcename A unique identifier for this source.  Two sources may not use the same sourcename.
 * @param filenameURL Filename/URL of the sound file to play at this source.
 * @param soundBuffer Buffer containing audio data, or null if not loaded yet.
 * @param x X position for this source.
 * @param y Y position for this source.
 * @param z Z position for this source.
 * @param attModel Attenuation model to use.
 * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of 'att'.
 * @param temporary Whether or not to remove this source after it finishes playing.
 */
    public void changeSource( Vector3D listenerPosition, int[] myBuffer,
                              boolean priority, boolean toStream,
                              boolean toLoop, String sourcename,
                              FilenameURL filenameURL, SoundBuffer soundBuffer,
                              float x, float y, float z, int attModel,
                              float distOrRoll, boolean temporary )
    {
        super.changeSource( priority, toStream, toLoop, sourcename, filenameURL,
                            soundBuffer, x, y, z, attModel, distOrRoll,
                            temporary );
        this.listenerPosition = listenerPosition;
        this.myBuffer = myBuffer;
        pitch = 1.0f;
        positionChanged();
    }
    
/**
 * Removes the next filename from the sound sequence queue and assigns it to
 * this source.  This method has no effect on non-streaming sources.  This
 * method is used internally by SoundSystem, and it is unlikely that the user
 * will ever need to use it.
 * @return True if there was something in the queue.
 */
    @Override
    public boolean incrementSoundSequence()
    {
        if( !toStream )
        {
            errorMessage( "Method 'incrementSoundSequence' may only be used " +
                          "for streaming sources." );
            return false;
        }
        synchronized( soundSequenceLock )
        {
            if( soundSequenceQueue != null && soundSequenceQueue.size() > 0 )
            {
                filenameURL = (FilenameURL) soundSequenceQueue.remove( 0 );
                if( codec != null )
                    codec.cleanup();
                codec = SoundSystemConfig.getCodec( filenameURL.getFilename() );
                if( codec != null )
                {
                    codec.reverseByteOrder( true );
                    if( codec.getAudioFormat() == null )
                        codec.initialize( filenameURL.getURL() );

                    AudioFormat audioFormat = codec.getAudioFormat();

                    if( audioFormat == null )
                    {
                        errorMessage( "Audio Format null in method " +
                                      "'incrementSoundSequence'" );
                        return false;
                    }

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
                                          "'incrementSoundSequence'" );
                            return false;
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
                                          "'incrementSoundSequence'" );
                            return false;
                        }
                    }
                    else
                    {
                        errorMessage( "Audio data neither mono nor stereo in " +
                                      "method 'incrementSoundSequence'" );
                        return false;
                    }

                    // Let the channel know what format and sample rate to use:
                    channelOpenAL.setFormat( soundFormat,
                                            (int) audioFormat.getSampleRate() );
                    preLoad = true;
                }
                return true;
            }
        }
        return false;
    }

/**
 * Called every time the listener's position or orientation changes.
 */
    @Override
    public void listenerMoved()
    {
        positionChanged();
    }
    
/**
 * Moves the source to the specified position.
 * @param x X coordinate to move to.
 * @param y Y coordinate to move to.
 * @param z Z coordinate to move to.
 */
    @Override
    public void setPosition( float x, float y, float z )
    {
        super.setPosition( x, y, z );
        
        positionChanged();
        
        // check if we are assigned to a channel.  If so, tell OpenAL where
        // this source is located:
        if( channel != null && channel.attachedSource == this &&
            channelOpenAL != null && channelOpenAL.ALSource != null )
        {
            // move the source:
            al.alSource3f( channelOpenAL.ALSource[0], AL.AL_POSITION,
                           x, y, z );
            checkALError();
        }
    }
    
/**
 * Recalculates the distance from the listner and the gain.
 */
    @Override
    public void positionChanged()
    {
        calculateDistance();
        calculateGain();
        
        if( channel != null && channel.attachedSource == this &&
            channelOpenAL != null && channelOpenAL.ALSource != null )
        {
            al.alSourcef( channelOpenAL.ALSource[0], AL.AL_GAIN,
                          (gain * sourceVolume * (float) Math.abs( fadeOutGain )
                                               * fadeInGain) );
            checkALError();
        }
        checkPitch();
    }
    
/**
 * Checks the source's pitch.
 */
    private void checkPitch()
    {
        if( channel != null && channel.attachedSource == this &&
            LibraryJOAL.alPitchSupported() && channelOpenAL != null &&
            channelOpenAL.ALSource != null )
        {
            al.alSourcef( channelOpenAL.ALSource[0],
                          AL.AL_PITCH, pitch );
            checkALError();
        }
    }

/**
 * Sets whether this source should loop or only play once.
 * @param lp True or false.
 */
    @Override
    public void setLooping( boolean lp )
    {
        super.setLooping( lp );
        
        // make sure we are assigned to a channel:
        if( channel != null && channel.attachedSource == this &&
            channelOpenAL != null && channelOpenAL.ALSource != null )
        {
            if( lp )
                al.alSourcei( channelOpenAL.ALSource[0], AL.AL_LOOPING,
                              AL.AL_TRUE );
            else
                al.alSourcei( channelOpenAL.ALSource[0], AL.AL_LOOPING,
                              AL.AL_FALSE );
            checkALError();
        }
    }
    
/**
 * Sets this source's attenuation model.
 * @param model Attenuation model to use.
 */
    @Override
    public void setAttenuation( int model )
    {
        super.setAttenuation( model );
        // make sure we are assigned to a channel:
        if( channel != null && channel.attachedSource == this &&
            channelOpenAL != null && channelOpenAL.ALSource != null )
        {
            // attenuation changed, so update the rolloff factor accordingly
            if( model == SoundSystemConfig.ATTENUATION_ROLLOFF )
                al.alSourcef( channelOpenAL.ALSource[0], AL.AL_ROLLOFF_FACTOR,
                              distOrRoll );
            else
                al.alSourcef( channelOpenAL.ALSource[0], AL.AL_ROLLOFF_FACTOR,
                              0.0f );
            checkALError();
        }
    }
    
/**
 * Sets this source's fade distance or rolloff factor, depending on the 
 * attenuation model.
 * @param dr New value for fade distance or rolloff factor.
 */
    @Override
    public void setDistOrRoll( float dr)
    {
        super.setDistOrRoll( dr );
        // make sure we are assigned to a channel:
        if( channel != null && channel.attachedSource == this &&
            channelOpenAL != null && channelOpenAL.ALSource != null )
        {
            // if we are using rolloff attenuation, then dr is a rolloff factor:
            if( attModel == SoundSystemConfig.ATTENUATION_ROLLOFF )
                al.alSourcef( channelOpenAL.ALSource[0], AL.AL_ROLLOFF_FACTOR,
                              dr );
            else
                al.alSourcef( channelOpenAL.ALSource[0], AL.AL_ROLLOFF_FACTOR,
                              0.0f );
            checkALError();
        }
    }

/**
 * Manually sets this source's pitch.
 * @param value A float value ( 0.5f - 2.0f ).
 */
    @Override
    public void setPitch( float value )
    {
        super.setPitch( value );
        checkPitch();
    }

/**
 * Plays the source on the specified channel.
 * @param c Channel to play on.
 */
    @Override
    public void play( Channel c )
    {
        if( !active() )
        {
            if( toLoop )
                toPlay = true;
            return;
        }
        
        if( c == null )
        {
            errorMessage( "Unable to play source, because channel was null" );
            return;
        }
        
        boolean newChannel = (channel != c);
        if( channel != null && channel.attachedSource != this )
            newChannel = true;

        boolean wasPaused = paused();
        
        super.play( c );
        
        channelOpenAL = (ChannelJOAL) channel;
        
        // Make sure the channel exists:
        // check if we are already on this channel:
        if( newChannel )
        {
            setPosition( position.x, position.y, position.z );
            checkPitch();
            
            // Send the source's attributes to the channel:
            if( channelOpenAL != null && channelOpenAL.ALSource != null )
            {
                if( LibraryJOAL.alPitchSupported() )
                {
                    al.alSourcef( channelOpenAL.ALSource[0], AL.AL_PITCH,
                                  pitch );
                    checkALError();
                }
                al.alSource3f( channelOpenAL.ALSource[0], AL.AL_POSITION,
                               position.x, position.y, position.z );
                checkALError();
                if( attModel == SoundSystemConfig.ATTENUATION_ROLLOFF )
                    al.alSourcef( channelOpenAL.ALSource[0],
                                  AL.AL_ROLLOFF_FACTOR, distOrRoll );
                else
                    al.alSourcef( channelOpenAL.ALSource[0],
                                  AL.AL_ROLLOFF_FACTOR, 0.0f );
                checkALError();
                if( toLoop && (!toStream) )
                    al.alSourcei( channelOpenAL.ALSource[0],
                                  AL.AL_LOOPING, AL.AL_TRUE );
                else
                    al.alSourcei( channelOpenAL.ALSource[0], AL.AL_LOOPING,
                                  AL.AL_FALSE );
                checkALError();
            }
            if( !toStream )
            {
                // This is not a streaming source, so make sure there is
                // a sound buffer loaded to play:
                if( myBuffer == null )
                {
                    errorMessage( "No sound buffer to play" );
                    return;
                }
                
                channelOpenAL.attachBuffer( myBuffer );
            }
        }
        
        // See if we are already playing:
        if( !playing() )
        {
            if( toStream && !wasPaused )
            {
                if( codec == null )
                {
                    errorMessage( "Decoder null in method 'play'" );
                    return;
                }
                if( codec.getAudioFormat() == null )
                    codec.initialize( filenameURL.getURL() );

                AudioFormat audioFormat = codec.getAudioFormat();
                
                if( audioFormat == null )
                {
                    errorMessage( "Audio Format null in method 'play'" );
                    return;
                }

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
                        errorMessage( "Illegal sample size in method 'play'" );
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
                        errorMessage( "Illegal sample size in method 'play'" );
                        return;
                    }
                }
                else
                {
                    errorMessage( "Audio data neither mono nor stereo in " +
                                  "method 'play'" );
                    return;
                }

                // Let the channel know what format and sample rate to use:
                channelOpenAL.setFormat( soundFormat,
                                         (int) audioFormat.getSampleRate() );
                preLoad = true;
            }
            channel.play();
            if( pitch != 1.0f )
                checkPitch();
        }
    }
    
/**
 * Queues up the initial stream-buffers for the stream.
 * @return False if the end of the stream was reached.
 */
    @Override
    public boolean preLoad()
    {
        if( codec == null )
            return false;
        
        codec.initialize( filenameURL.getURL() );
        LinkedList<byte[]> preLoadBuffers = new LinkedList<byte[]>();
        for( int i = 0; i < SoundSystemConfig.getNumberStreamingBuffers(); i++ )
        {
            soundBuffer = codec.read();

            if( soundBuffer == null || soundBuffer.audioData == null )
                break;

            preLoadBuffers.add( soundBuffer.audioData );
        }
        positionChanged();
        
        channel.preLoadBuffers( preLoadBuffers );        

        preLoad = false;
        return true;
    }
    
/**
 * Calculates this source's distance from the listener.
 */
    private void calculateDistance()
    {
        if( listenerPosition != null )
        {
            // Calculate the source's distance from the listener:
            double dX = position.x - listenerPosition.x;
            double dY = position.y - listenerPosition.y;
            double dZ = position.z - listenerPosition.z;
            distanceFromListener = (float) Math.sqrt( dX*dX + dY*dY + dZ*dZ );
        }
    }
    
/**
 * If using linear attenuation, calculates the gain for this source based on 
 * its distance from the listener.
 */
    private void calculateGain()
    {
        // If using linear attenuation, calculate the source's gain:
        if( attModel == SoundSystemConfig.ATTENUATION_LINEAR )
        {
            if( distanceFromListener <= 0 )
            {
                gain = 1.0f;
            }
            else if( distanceFromListener >= distOrRoll )
            {
                gain = 0.0f;
            }
            else
            {
                gain = 1.0f - (distanceFromListener / distOrRoll);
            }
            if( gain > 1.0f )
                gain = 1.0f;
            if( gain < 0.0f )
                gain = 0.0f;
        }
        else
        {
            gain = 1.0f;
        }
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
