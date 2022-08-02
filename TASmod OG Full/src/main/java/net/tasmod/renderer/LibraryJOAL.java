package net.tasmod.renderer;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.sound.sampled.AudioFormat;

// From the joal library, http://joal.dev.java.net/
import com.jogamp.openal.AL;
import com.jogamp.openal.ALC;
import com.jogamp.openal.ALCcontext;
import com.jogamp.openal.ALException;
import com.jogamp.openal.ALExt;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

import net.tasmod.TASmod;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.ICodec;
import paulscode.sound.Library;
import paulscode.sound.ListenerData;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.Source;

/**
 * The LibraryJOAL class interfaces the JOAL binding of OpenAL.
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
@SuppressWarnings("unchecked")
public class LibraryJOAL extends Library
{
/**
 * Used to return a current value from one of the synchronized
 * boolean-interface methods.
 */
    private static final boolean GET = false;
/**
 * Used to set the value in one of the synchronized boolean-interface methods.
 */
    private static final boolean SET = true;
/**
 * Used when a parameter for one of the synchronized boolean-interface methods
 * is not aplicable.
 */
    private static final boolean XXX = false;

/**
 * Handle for accessing OpenAL.
 */
    private static AL al = null;

/**
 * Map containing OpenAL identifiers for sound buffers.
 */
    private HashMap<String, int[]> ALBufferMap = null;

/**
 * Whether or not the AL_PITCH control is supported.
 */
    private static boolean alPitchSupported = true;
    
/**
 * Constructor: Instantiates the source map, buffer map and listener 
 * information.  Also sets the library type to 
 * SoundSystemConfig.LIBRARY_OPENAL
 */
    public LibraryJOAL() throws SoundSystemException
    {
        super();
        ALBufferMap = new HashMap<String, int[]>();
//        reverseByteOrder = true;
    }
    
/**
 * Initializes OpenAL, creates the listener, and grabs up audio channels. 
 */
    @Override
    public void init() {
        boolean errors = false; // set to 'true' if error(s) occur:
        
        try
        {
        	if (TASmod.playback != null && !TASmod.playback.isVisible()) {
            	ALut.alutInit();
            	ALExt ext = ALFactory.getALExt();
            	ALC alc = ALFactory.getALC();
            	
            	Renderer.dev = ext.alcLoopbackOpenDeviceSOFT(null);
            	Renderer.ext = ext;
            	ALCcontext ctx = alc.alcCreateContext(Renderer.dev, new int[] { ALExt.ALC_FORMAT_CHANNELS_SOFT, ALExt.ALC_STEREO_SOFT, ALExt.ALC_FORMAT_TYPE_SOFT, ALExt.ALC_FLOAT_SOFT, ALC.ALC_FREQUENCY, 44100, 0 }, 0);
                alc.alcMakeContextCurrent(ctx);
            	
                al = ALFactory.getAL();        		
        	} else {
              ALut.alutInit();  // creates an OpenAL context.
              
              // Grab a handle to use for accessing OpenAL:
              al = ALFactory.getAL();
              errors = checkALError();	
        	}
        }
        catch( ALException e )
        {
            // There was an exception
            errorMessage( "Unable to initialize OpenAL.  Probable cause: " +
                          "OpenAL not supported." );
            printStackTrace( e );
        }
        
        // Let user know if the library loaded properly
        if( errors )
            importantMessage( "OpenAL did not initialize properly!" );
        else
            message( "OpenAL initialized." );
        
        // Pass the listener info to the sound system, and check for errors:
        al.alListener3f( AL.AL_POSITION, listener.position.x,
                         listener.position.y, listener.position.z );
        errors = checkALError() || errors;

        al.alListenerfv( AL.AL_ORIENTATION, new float[]{ listener.lookAt.x,
                                                         listener.lookAt.y,
                                                         listener.lookAt.z,
                                                         listener.up.x,
                                                         listener.up.y,
                                                         listener.up.z }, 0 );
        errors = checkALError() || errors;

        // Let user know what caused the above error messages:
        if( errors )
        {
            importantMessage( "OpenAL did not initialize properly!" );
        }
        
        super.init();

        // Check if we can use the AL_PITCH control:
        ChannelJOAL channel = (ChannelJOAL) normalChannels.get( 1 );
        try
        {
            al.alSourcef( channel.ALSource[0], AL.AL_PITCH, 1.0f );
            if( checkALError() )
            {
                alPitchSupported( SET, false );
                throw new LibraryJOAL.Exception( "OpenAL: AL_PITCH not " +
                               "supported.", LibraryJOAL.Exception.NO_AL_PITCH );
            }
            else
            {
                alPitchSupported( SET, true );
            }
        }
        catch( Exception e )
        {
            alPitchSupported( SET, false );
        }
    }
    
/**
 * Checks if the OpenAL library type is compatible.
 * @return True or false.
 */
    public static boolean libraryCompatible()
    {
        try
        {
            al = ALFactory.getAL();
            if( al != null )
                return true;
        }
        catch( ALException e )
        {}

        boolean compatible = true;

        try
        {
            ALut.alutInit();
        }
        catch( ALException e )
        {
            compatible = false;
        }
        
        try
        {
            ALut.alutExit();
            al = null;
        }
        catch( java.lang.Exception e )
        {}
        
        return compatible;
    }
    
/**
 * Creates a new channel of the specified type (normal or streaming).  Possible 
 * values for channel type can be found in the 
 * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} class.
 * @param type Type of channel.
 */
    @Override
    protected Channel createChannel( int type )
    {
        ChannelJOAL channel;

        int[] ALSource = new int[1];
        try
        {
            al.alGenSources( 1, ALSource, 0 );
        }
        catch( java.lang.Exception e )
        {
            al.alGetError();
            return null;  // no more voices left
        }

        if( al.alGetError() != AL.AL_NO_ERROR )
            return null;

        channel = new ChannelJOAL( type, ALSource );
        return channel;
    }
    
 /**
 * Stops all sources, shuts down OpenAL, and removes references to all 
 * instantiated objects.
 */
	@Override
    public void cleanup()
    {
        super.cleanup();
        
        Set<String> keys = bufferMap.keySet();
        Iterator<String> iter = keys.iterator();        
        String filename;
        int[] buffer;
        
        // loop through and clear all sound buffers:
        while( iter.hasNext() )
        {
            filename = iter.next();
            buffer = ALBufferMap.get( filename );
            if( buffer != null )
            {
                al.alDeleteBuffers( 1, buffer, 0 );
                checkALError();
                buffer = null;
            }
        }
        
        try
        {
            ALut.alutExit();
            al = null;
        }
        catch( java.lang.Exception e )
        {}
        
        bufferMap.clear();
        bufferMap = null;
    }

/**
 * Pre-loads a sound into memory.
 * @param filenameURL Filename/URL of the sound file to load.
 * @return True if the sound loaded properly.
 */
    @Override
    public boolean loadSound( FilenameURL filenameURL )
    {
        // Make sure the buffer map exists:
        if( bufferMap == null )
        {
            bufferMap = new HashMap<String, SoundBuffer>();
            importantMessage( "Buffer Map was null in method 'loadSound'" );
        }
        // Make sure the OpenAL buffer map exists:
        if( ALBufferMap == null )
        {
            ALBufferMap = new HashMap<String, int[]>();
            importantMessage( "Open AL Buffer Map was null in method" +
                              "'loadSound'" );
        }
        
        // make sure they gave us a filename:
        if( errorCheck( filenameURL == null,
                          "Filename/URL not specified in method 'loadSound'" ) )
            return false;
        
        // check if it is already loaded:        
        if( bufferMap.get( filenameURL.getFilename() ) != null )
            return true;
        
        ICodec codec = SoundSystemConfig.getCodec( filenameURL.getFilename() );
        if( errorCheck( codec == null, "No codec found for file '" +
                                       filenameURL.getFilename() +
                                       "' in method 'loadSound'" ) )
            return false;
        codec.reverseByteOrder( true );

        URL url = filenameURL.getURL();
        if( errorCheck( url == null, "Unable to open file '" +
                                     filenameURL.getFilename() +
                                     "' in method 'loadSound'" ) )
            return false;

        codec.initialize( url );
        SoundBuffer buffer = codec.readAll();
        codec.cleanup();
        codec = null;
        if( errorCheck( buffer == null,
                                   "Sound buffer null in method 'loadSound'" ) )
            return false;

        bufferMap.put( filenameURL.getFilename(), buffer );

        AudioFormat audioFormat = buffer.audioFormat;
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
                errorMessage( "Illegal sample size in method 'loadSound'" );
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
                errorMessage( "Illegal sample size in method 'loadSound'" );
                return false;
            }
        }
        else
        {
            errorMessage( "File neither mono nor stereo in method " +
                          "'loadSound'" );
            return false;
        }

        int[] intBuffer = new int[1];
        al.alGenBuffers( 1, intBuffer, 0 );
        if( errorCheck( checkALError(),
                        "alGenBuffers error when loading " +
                        filenameURL.getFilename() ) )
            return false;
        
        al.alBufferData( intBuffer[0], soundFormat,
                           ByteBuffer.wrap( buffer.audioData ),
                           buffer.audioData.length,
                           (int) audioFormat.getSampleRate() );
        if( errorCheck( checkALError(),
                        "alBufferData error when loading " +
                        filenameURL.getFilename() ) )
        
                
        if( errorCheck( intBuffer == null,
                        "Sound buffer was not created for " +
                        filenameURL.getFilename() ) )
            return false;
        
        ALBufferMap.put( filenameURL.getFilename(), intBuffer );
        
        return true;
    }

//    /**
// * Saves the specified sample data, under the specified identifier.  This
// * identifier can be later used in place of 'filename' parameters to reference
// * the sample data.
// * @param buffer the sample data and audio format to save.
// * @param identifier What to call the sample.
// * @return True if there weren't any problems.
// */
//    @Override
//    public boolean loadSound( SoundBuffer buffer, String identifier )
//    {
//        // Make sure the buffer map exists:
//        if( bufferMap == null )
//        {
//            bufferMap = new HashMap<String, SoundBuffer>();
//            importantMessage( "Buffer Map was null in method 'loadSound'" );
//        }
//        // Make sure the OpenAL buffer map exists:
//        if( ALBufferMap == null )
//        {
//            ALBufferMap = new HashMap<String, int[]>();
//            importantMessage( "Open AL Buffer Map was null in method" +
//                              "'loadSound'" );
//        }
//
//        // make sure they gave us an identifier:
//        if( errorCheck( identifier == null,
//                          "Identifier not specified in method 'loadSound'" ) )
//            return false;
//
//        // check if it is already loaded:
//        if( bufferMap.get( identifier ) != null )
//            return true;
//
//        if( errorCheck( buffer == null,
//                                   "Sound buffer null in method 'loadSound'" ) )
//            return false;
//
//        bufferMap.put( identifier, buffer );
//
//        AudioFormat audioFormat = buffer.audioFormat;
//        int soundFormat = 0;
//        if( audioFormat.getChannels() == 1 )
//        {
//            if( audioFormat.getSampleSizeInBits() == 8 )
//            {
//                soundFormat = AL.AL_FORMAT_MONO8;
//            }
//            else if( audioFormat.getSampleSizeInBits() == 16 )
//            {
//                soundFormat = AL.AL_FORMAT_MONO16;
//            }
//            else
//            {
//                errorMessage( "Illegal sample size in method 'loadSound'" );
//                return false;
//            }
//        }
//        else if( audioFormat.getChannels() == 2 )
//        {
//            if( audioFormat.getSampleSizeInBits() == 8 )
//            {
//                soundFormat = AL.AL_FORMAT_STEREO8;
//            }
//            else if( audioFormat.getSampleSizeInBits() == 16 )
//            {
//                soundFormat = AL.AL_FORMAT_STEREO16;
//            }
//            else
//            {
//                errorMessage( "Illegal sample size in method 'loadSound'" );
//                return false;
//            }
//        }
//        else
//        {
//            errorMessage( "File neither mono nor stereo in method " +
//                          "'loadSound'" );
//            return false;
//        }
//
//        int[] intBuffer = new int[1];
//        al.alGenBuffers( 1, intBuffer, 0 );
//        if( errorCheck( checkALError(),
//                        "alGenBuffers error when saving " +
//                        identifier ) )
//            return false;
//
//        al.alBufferData( intBuffer[0], soundFormat,
//                           ByteBuffer.wrap( buffer.audioData ),
//                           buffer.audioData.length,
//                           (int) audioFormat.getSampleRate() );
//        if( errorCheck( checkALError(),
//                        "alBufferData error when saving " +
//                        identifier ) )
//
//
//        if( errorCheck( intBuffer == null,
//                        "Sound buffer was not created for " +
//                        identifier ) )
//            return false;
//
//        ALBufferMap.put( identifier, intBuffer );
//
//        return true;
//    }
    
/**
 * Removes a pre-loaded sound from memory.  This is a good method to use for 
 * freeing up memory after a large sound file is no longer needed.  NOTE: the 
 * source will remain in memory after this method has been called, for as long 
 * as the sound is attached to an existing source.
 * @param filename Filename/identifier of the sound file to unload.
 */
    @Override
    public void unloadSound( String filename )
    {
        ALBufferMap.remove( filename );
        super.unloadSound( filename );
    }
    
 /**
 * Sets the overall volume to the specified value, affecting all sources.
 * @param value New volume, float value ( 0.0f - 1.0f ).
 */ 
    @Override
    public void setMasterVolume( float value )
    {
        super.setMasterVolume( value );
        
        al.alListenerf( AL.AL_GAIN, value );
        checkALError();
    }
    
/**
 * Creates a new source and places it into the source map.
 * @param priority Setting this to true will prevent other sounds from overriding this one.
 * @param toStream Setting this to true will load the sound in pieces rather than all at once.
 * @param toLoop Should this source loop, or play only once.
 * @param sourcename A unique identifier for this source.  Two sources may not use the same sourcename.
 * @param filenameURL Filename/URL of the sound file to play at this source.
 * @param x X position for this source.
 * @param y Y position for this source.
 * @param z Z position for this source.
 * @param attModel Attenuation model to use.
 * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
 */
    @Override
    public void newSource( boolean priority, boolean toStream, boolean toLoop,
                           String sourcename, FilenameURL filenameURL, float x,
                           float y, float z, int attModel,
                           float distOrRoll )
    {
        int[] myBuffer = null;
        if( !toStream )
        {
            // Grab the sound buffer for this file:
            myBuffer = ALBufferMap.get( filenameURL.getFilename() );
            
            // if not found, try loading it:
            if( myBuffer == null )
            {
                if( !loadSound( filenameURL ) )
                {
                    errorMessage( "Source '" + sourcename + "' was not created "
                                  + "because an error occurred while loading "
                                  + filenameURL.getFilename() );
                    return;
                }
            }

            // try and grab the sound buffer again:
            myBuffer = ALBufferMap.get( filenameURL.getFilename() );
            // see if it was there this time:
            if( myBuffer == null )
            {
                errorMessage( "Source '" + sourcename + "' was not created "
                              + "because a sound buffer was not found for "
                              + filenameURL.getFilename() );
                return;
            }
        }
        SoundBuffer buffer = null;
        
        if( !toStream )
        {
            // Grab the audio data for this file:
            buffer = (SoundBuffer) bufferMap.get( filenameURL.getFilename() );
            // if not found, try loading it:
            if( buffer == null )
            {
                if( !loadSound( filenameURL ) )
                {
                    errorMessage( "Source '" + sourcename + "' was not created "
                                  + "because an error occurred while loading "
                                  + filenameURL.getFilename() );
                    return;
                }
            }
            // try and grab the sound buffer again:
            buffer = (SoundBuffer) bufferMap.get( filenameURL.getFilename() );
            // see if it was there this time:
            if( buffer == null )
            {
                errorMessage( "Source '" + sourcename + "' was not created "
                              + "because audio data was not found for "
                              + filenameURL.getFilename() );
                return;
            }
        }
        
        sourceMap.put( sourcename,
                       new SourceJOAL( listener.position, myBuffer, priority,
                                       toStream, toLoop, sourcename,
                                       filenameURL, buffer, x, y, z, attModel,
                                       distOrRoll, false ) );
    }

/**
 * Opens a direct line for streaming audio data.
 * @param audioFormat Format that the data will be in.
 * @param priority Setting this to true will prevent other sounds from overriding this one.
 * @param sourcename A unique identifier for this source.  Two sources may not use the same sourcename.
 * @param x X position for this source.
 * @param y Y position for this source.
 * @param z Z position for this source.
 * @param attModel Attenuation model to use.
 * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
 */
    @Override
    public void rawDataStream( AudioFormat audioFormat, boolean priority,
                               String sourcename, float x, float y,
                               float z, int attModel, float distOrRoll )
    {
        sourceMap.put( sourcename,
                       new SourceJOAL( listener.position, audioFormat, priority,
                                       sourcename, x, y, z, attModel,
                                       distOrRoll ) );
    }
    
/**
 * Creates and immediately plays a new source.
 * @param priority Setting this to true will prevent other sounds from overriding this one.
 * @param toStream Setting this to true will load the sound in pieces rather than all at once.
 * @param toLoop Should this source loop, or play only once.
 * @param sourcename A unique identifier for this source.  Two sources may not use the same sourcename.
 * @param filenameURL Filename/URL of the sound file to play at this source.
 * @param x X position for this source.
 * @param y Y position for this source.
 * @param z Z position for this source.
 * @param attModel Attenuation model to use.
 * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
 * @param temporary Whether or not this source should be removed after it finishes playing.
 */
    @Override
    public void quickPlay( boolean priority, boolean toStream, boolean toLoop,
                           String sourcename, FilenameURL filenameURL, float x,
                           float y, float z, int attModel, float distOrRoll,
                           boolean temporary )
    {
        int[] myBuffer = null;
        if( !toStream )
        {
            // Grab the sound buffer for this file:
            myBuffer = ALBufferMap.get( filenameURL.getFilename() );
            // if not found, try loading it:
            if( myBuffer == null )
                loadSound( filenameURL );
            // try and grab the sound buffer again:
            myBuffer = ALBufferMap.get( filenameURL.getFilename() );
            // see if it was there this time:
            if( myBuffer == null )
            {
                errorMessage( "Sound buffer was not created for " +
                              filenameURL.getFilename() );
                return;
            }
        }
        
        SoundBuffer buffer = null;
        
        if( !toStream )
        {
            // Grab the sound buffer for this file:
            buffer = (SoundBuffer) bufferMap.get( filenameURL.getFilename() );
            // if not found, try loading it:
            if( buffer == null )
            {
                if( !loadSound( filenameURL ) )
                {
                    errorMessage( "Source '" + sourcename + "' was not created "
                                  + "because an error occurred while loading "
                                  + filenameURL.getFilename() );
                    return;
                }
            }
            // try and grab the sound buffer again:
            buffer = (SoundBuffer) bufferMap.get( filenameURL.getFilename() );
            // see if it was there this time:
            if( buffer == null )
            {
                errorMessage( "Source '" + sourcename + "' was not created "
                              + "because audio data was not found for "
                              + filenameURL.getFilename() );
                return;
            }
        }
        
        sourceMap.put( sourcename,
                       new SourceJOAL( listener.position, myBuffer, priority,
                                       toStream, toLoop, sourcename,
                                       filenameURL, buffer, x, y, z, attModel,
                                       distOrRoll, temporary ) );
    }
    
/**1
 * Creates sources based on the source map provided.
 * @param srcMap Sources to copy.
 */
    @Override
    public void copySources( @SuppressWarnings("rawtypes") HashMap srcMap )
    {
        if( srcMap == null )
            return;
        Set<String> keys = srcMap.keySet();
        Iterator<String> iter = keys.iterator();        
        String sourcename;
        Source source;
        
        // Make sure the buffer map exists:
        if( bufferMap == null )
        {
            bufferMap = new HashMap<String, SoundBuffer>();
            importantMessage( "Buffer Map was null in method 'copySources'" );
        }
        // Make sure the OpenAL buffer map exists:
        if( ALBufferMap == null )
        {
            ALBufferMap = new HashMap<String, int[]>();
            importantMessage( "Open AL Buffer Map was null in method" +
                              "'copySources'" );
        }
        
        // remove any existing sources before starting:
        sourceMap.clear();
        
        SoundBuffer buffer;
        // loop through and copy all the sources:
        while( iter.hasNext() )
        {
            sourcename = iter.next();
            source = (Source) srcMap.get( sourcename );
            if( source != null )
            {
                buffer = null;
                if( !source.toStream )
                {
                    loadSound( source.filenameURL );
                    buffer = (SoundBuffer) bufferMap.get( source.filenameURL.getFilename() );
                }
                if( source.toStream || buffer != null )
                    sourceMap.put( sourcename, new SourceJOAL(
                                   listener.position,
                                   ALBufferMap.get(
                                             source.filenameURL.getFilename() ),
                                   source, buffer ) );
            }
        }
    }
    
/**
 * Changes the listener's position. 
 * @param x Destination X coordinate.
 * @param y Destination Y coordinate.
 * @param z Destination Z coordinate.
 */
    @Override
    public void setListenerPosition( float x, float y, float z )
    {
        super.setListenerPosition( x, y, z );
        
        // Update OpenAL listener position:
        al.alListener3f( AL.AL_POSITION, x, y, z );
        // Check for errors:
        checkALError();
    }
    
/**
 * Changes the listeners orientation to the specified 'angle' radians 
 * counterclockwise around the y-Axis.
 * @param angle Radians.
 */
    @Override
    public void setListenerAngle( float angle )
    {
        super.setListenerAngle( angle );
        
        // Update OpenAL listener orientation:
        al.alListenerfv( AL.AL_ORIENTATION, new float[]{ listener.lookAt.x,
                                                         listener.lookAt.y,
                                                         listener.lookAt.z,
                                                         listener.up.x,
                                                         listener.up.y,
                                                         listener.up.z }, 0 );
        // Check for errors:
        checkALError();
    }
    
/**
 * Changes the listeners orientation using the specified coordinates.
 * @param lookX X element of the look-at direction.
 * @param lookY Y element of the look-at direction.
 * @param lookZ Z element of the look-at direction.
 * @param upX X element of the up direction.
 * @param upY Y element of the up direction.
 * @param upZ Z element of the up direction.
 */
    @Override
    public void setListenerOrientation( float lookX, float lookY, float lookZ,
                                        float upX, float upY, float upZ )
    {
        super.setListenerOrientation( lookX, lookY, lookZ, upX, upY, upZ );

        // Update OpenAL listener orientation:
        al.alListenerfv( AL.AL_ORIENTATION, new float[]{ listener.lookAt.x,
                                                         listener.lookAt.y,
                                                         listener.lookAt.z,
                                                         listener.up.x,
                                                         listener.up.y,
                                                         listener.up.z }, 0 );
        // Check for errors:
        checkALError();
    }
    
/**
 * Changes the listeners position and orientation using the specified listener 
 * data.
 * @param l Listener data to use.
 */
    @Override
    public void setListenerData( ListenerData l )
    {
        super.setListenerData( l );
        
        // Pass the listener info to the sound system, and check for errors:
        al.alListener3f( AL.AL_POSITION, listener.position.x,
                         listener.position.y, listener.position.z );
        checkALError();
        al.alListenerfv( AL.AL_ORIENTATION, new float[]{ listener.lookAt.x,
                                                         listener.lookAt.y,
                                                         listener.lookAt.z,
                                                         listener.up.x,
                                                         listener.up.y,
                                                         listener.up.z }, 0 );
        checkALError();
    }
    
/**
 * Returns a handle to OpenAL, or null if OpenAL is not initialized.
 * @return Used to interface with OpenAL functions.
 */
    public static AL getAL()
    {
        return al;
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

/**
 * Whether or not the AL_PITCH control is supported.
 * @return True if AL_PITCH is supported.
 */
    public static boolean alPitchSupported()
    {
        return alPitchSupported( GET, XXX );
    }
/**
 * Sets or returns the value of boolean 'alPitchSupported'.
 * @param action Action to perform (GET or SET).
 * @param value New value if action is SET, otherwise XXX.
 * @return value of boolean 'alPitchSupported'.
 */
    private static synchronized boolean alPitchSupported( boolean action,
                                                          boolean value )
    {
        if( action == SET )
            alPitchSupported = value;
        return alPitchSupported;
    }

/**
 * Returns the short title of this library type.
 * @return A short title.
 */
    public static String getTitle()
    {
        return "JOAL";
    }

/**
 * Returns a longer description of this library type.
 * @return A longer description.
 */
    public static String getDescription()
    {
        return "The JOAL binding of OpenAL.  For more information, see " +
               "http://joal.dev.java.net/";
    }

/**
 * Returns the name of the class.
 * @return "Library" + library title.
 */
    @Override
    public String getClassName()
    {
        return "LibraryJOAL";
    }

/**
 * The LibraryJOAL.Exception class provides library-specific error information.
 */
    public static class Exception extends SoundSystemException
    {
        private static final long serialVersionUID = -7559402481189210355L;
        /**
         * Global identifier for an exception during AL.create().  Probably means
         * that OpenAL is not supported.
         */
        public static final int CREATE                           =  101;
        /**
         * Global identifier for an invalid name parameter in OpenAL.
         */
        public static final int INVALID_NAME                     =  102;
        /**
         * Global identifier for an invalid parameter in OpenAL.
         */
        public static final int INVALID_ENUM                     =  103;
        /**
         * Global identifier for an invalid enumerated parameter value in OpenAL.
         */
        public static final int INVALID_VALUE                     =  104;
        /**
         * Global identifier for an illegal call in OpenAL.
         */
        public static final int INVALID_OPERATION                 = 105;
        /**
         * Global identifier for OpenAL out of memory.
         */
        public static final int OUT_OF_MEMORY                    = 106;
        /**
         * Global identifier for an exception while creating the OpenAL Listener.
         */
        public static final int LISTENER                           = 107;
        /**
         * Global identifier for OpenAL AL_PITCH not supported.
         */
        public static final int NO_AL_PITCH                       = 108;

        /**
         * Constructor: Generates a standard "unknown error" exception with the
         * specified message.
         * @param message A brief description of the problem that occurred.
         */
        public Exception( String message )
        {
            super( message );
        }

        /**
         * Constructor: Generates an exception of the specified type, with the
         * specified message.
         * @param message A brief description of the problem that occurred.
         * @param type Identifier indicating they type of error.
         */
        public Exception( String message, int type )
        {
            super( message, type );
        }
    }
}
