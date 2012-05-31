/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.type.numeric.integer;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.NativeType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Albert Cardona
 */
public class UnsignedLongType extends AbstractIntegerType<UnsignedLongType> implements NativeType<UnsignedLongType>
{
	private int i = 0;

	final protected NativeImg<UnsignedLongType, ? extends LongAccess> img;

	// the DataAccess that holds the information
	protected LongAccess dataAccess;
	
	// this is the constructor if you want it to read from an array
	public UnsignedLongType( final NativeImg<UnsignedLongType, ? extends LongAccess> img )
	{
		this.img = img;
	}

	// this is the constructor if you want it to be a variable
	public UnsignedLongType( final long value )
	{
		img = null;
		dataAccess = new LongArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public UnsignedLongType( final LongAccess access )
	{
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public UnsignedLongType() { this( 0 ); }

	@Override
	public NativeImg<UnsignedLongType, ? extends LongAccess> createSuitableNativeImg( final NativeImgFactory<UnsignedLongType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<UnsignedLongType, ? extends LongAccess> container = storageFactory.createLongInstance( dim, 1 );

		// create a Type that is linked to the container
		final UnsignedLongType linkedType = new UnsignedLongType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public UnsignedLongType duplicateTypeOnSameNativeImg() { return new UnsignedLongType( img ); }

	@Override
	public void mul( final float c )
	{
		set( Util.round( (double) ( get() * c ) ) );
	}

	@Override
	public void mul( final double c )
	{
		set( Util.round( ( get() * c ) ) );
	}
	@Override
	public void add( final UnsignedLongType c )
	{
		set( get() + c.get() );
	}

	/**
	 * See "Division by Invariant Integers using Multiplication",
	 * by Torbjorn Granlund and Peter L. Montgomery, 1994.
	 * http://gmplib.org/~tege/divcnst-pldi94.pdf
	 * 
	 * @throws ArithmeticException when c equals zero.
	 */
	@Override
	public void div( final UnsignedLongType c )
	{
		final long d1 = get();
		final long d2 = c.get();
		
		if (d2 < 0) {
			// d2 is larger than the maximum signed long value
			if (-1 == compare(d1, d2)) {
				// d1 is smaller than d2
				set( 0L );
			} else {
				// d1 is larger than d2
				set( 1L );
			}
		} else if (d1 >= 0) {
			// Exact division, given that d2 is smaller than the maximum signed long value
			set( d1 / d2 );
		} else {
			// Approximate division: exact or one less than the actual value
			final long quotient = ((d1 >>> 1) / d2) << 1;
			final long reminder = d1 - quotient * d2;
			set( quotient + (-1 == compare(d2, reminder) ? 0 : 1) );
		}
	}

	@Override
	public void mul( final UnsignedLongType c )
	{
		set( get() * c.get() );
	}

	@Override
	public void sub( final UnsignedLongType c )
	{
		set( get() - c.get() );
	}

	@Override
	public void setOne() { set( 1 ); }

	@Override
	public void setZero() { set( 0 ); }

	@Override
	public void inc()
	{
		set( get() + 1 );
	}

	@Override
	public void dec()
	{
		set( get() - 1 );
	}

	@Override
	public String toString() { return "" + get(); }

	public long get() {
		return dataAccess.getValue( i );
	}
	public void set( final long value ) {
		dataAccess.setValue( i, value);
	}

	@Override
	public int getInteger() { return (int)get(); }
	@Override
	public long getIntegerLong() { return get(); }
	@Override
	public void setInteger( final int f ) { set( f ); }
	@Override
	public void setInteger( final long f ) { set( f ); }

	@Override
	public double getMaxValue() { return 0xffffffffL; }
	@Override
	public double getMinValue()  { return 0; }

	@Override
	public int compareTo( final UnsignedLongType c )
	{
		return compare( get(), c.get() );
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return -1 if a < b, 0 if a == b, 1 if a > b.
	 */
	static public final int compare( long a, long b) {
		if (a == b)
			return 0;
		else {
			boolean test = (a < b);
			if ((a < 0) != (b < 0)) {
				test = !test;
			}
			return test ? -1 : 1;
		}
	}

	@Override
	public UnsignedLongType createVariable() { return new UnsignedLongType( 0 ); }

	@Override
	public UnsignedLongType copy() { return new UnsignedLongType( get() ); }

	@Override
	public int getBitsPerPixel() {
		return 64;
	}

	@Override
	public int getEntitiesPerPixel() {
		return 1;
	}

	@Override
	public void updateContainer( final Object c ) { dataAccess = img.update( c ); }


	@Override
	public void updateIndex( final int index ) { i = index; }

	@Override
	public int getIndex() { return i; }

	@Override
	public void incIndex() { ++i; }

	@Override
	public void incIndex( final int increment ) { i += increment; }

	@Override
	public void decIndex() { --i; }

	@Override
	public void decIndex( final int decrement ) { i -= decrement; }
}
