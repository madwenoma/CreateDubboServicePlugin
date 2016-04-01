/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io.input;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.EndianUtils;

/**
 * DataInput for systems relying on little endian data formats.
 * When read, values will be changed from little endian to big 
 * endian formats for internal usage. 
 * <p>
 * <b>Origin of code: </b>Avalon Excalibur (IO)
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 437567 $ $Date: 2006-08-28 08:39:07 +0200 (Mo, 28 Aug 2006) $
 */
public class SwappedDataInputStream extends ProxyInputStream
    implements DataInput
{

    /**
     * Constructs a SwappedDataInputStream.
     *
     * @param input InputStream to read from
     */
    public SwappedDataInputStream( InputStream input )
    {
        super( input );
    }

    /** @see DataInput#readBoolean() */
    public boolean readBoolean()
        throws IOException, EOFException
    {
        return ( 0 == readByte() );
    }

    /** @see DataInput#readByte() */
    public byte readByte()
        throws IOException, EOFException
    {
        return (byte)in.read();
    }

    /** @see DataInput#readChar() */
    public char readChar()
        throws IOException, EOFException
    {
        return (char)readShort();
    }

    /** @see DataInput#readDouble() */
    public double readDouble()
        throws IOException, EOFException
    {
        return EndianUtils.readSwappedDouble( in );
    }

    /** @see DataInput#readFloat() */
    public float readFloat()
        throws IOException, EOFException
    {
        return EndianUtils.readSwappedFloat( in );
    }

    /** @see DataInput#readFully(byte[]) */
    public void readFully( byte[] data )
        throws IOException, EOFException
    {
        readFully( data, 0, data.length );
    }

    /** @see DataInput#readFully(byte[], int, int) */
    public void readFully( byte[] data, int offset, int length )
        throws IOException, EOFException
    {
        int remaining = length;

        while( remaining > 0 )
        {
            int location = offset + ( length - remaining );
            int count = read( data, location, remaining );

            if( -1 == count )
            {
                throw new EOFException();
            }

            remaining -= count;
        }
    }

    /** @see DataInput#readInt() */
    public int readInt()
        throws IOException, EOFException
    {
        return EndianUtils.readSwappedInteger( in );
    }

    /**
     * Not currently supported.
     *
     * @see DataInput#readLine()
     */
    public String readLine()
        throws IOException, EOFException
    {
        throw new UnsupportedOperationException( 
                "Operation not supported: readLine()" );
    }

    /** @see DataInput#readLong() */
    public long readLong()
        throws IOException, EOFException
    {
        return EndianUtils.readSwappedLong( in );
    }

    /** @see DataInput#readShort() */
    public short readShort()
        throws IOException, EOFException
    {
        return EndianUtils.readSwappedShort( in );
    }

    /** @see DataInput#readUnsignedByte() */
    public int readUnsignedByte()
        throws IOException, EOFException
    {
        return in.read();
    }

    /** @see DataInput#readUnsignedShort() */
    public int readUnsignedShort()
        throws IOException, EOFException
    {
        return EndianUtils.readSwappedUnsignedShort( in );
    }

    /**
     * Not currently supported. 
     *
     * @see DataInput#readUTF()
     */
    public String readUTF()
        throws IOException, EOFException
    {
        throw new UnsupportedOperationException( 
                "Operation not supported: readUTF()" );
    }

    /** @see DataInput#skipBytes(int) */
    public int skipBytes( int count )
        throws IOException, EOFException
    {
        return (int)in.skip( count );
    }

}
