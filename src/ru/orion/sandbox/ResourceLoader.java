package ru.orion.sandbox;

import static java.io.BufferedReader.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.EXTTextureArray.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import ru.orion.sandbox.Renderer.Texture;
import ru.orion.sandbox.Renderer.TextureAtlas;


public class ResourceLoader {
	public static TextureAtlas LoadTextureAtlas(String filename,short[] id) {
		TextureAtlas tex = new TextureAtlas(filename,id,32,32);
		tex.setTextureArray(glGenTextures());
		int width;
		int height;
		try {
			BufferedInputStream input = new BufferedInputStream(new FileInputStream("./resources/textures/"+filename));
			PNGDecoder decoder = new PNGDecoder(input);
			height = decoder.getHeight();
			width = decoder.getWidth();
			ByteBuffer buf = ByteBuffer.allocateDirect(4*decoder.getHeight()*decoder.getWidth());
			ByteBuffer temp = ByteBuffer.allocateDirect(4*tex.getTexUnitWidght()*tex.getTexUnitWidght());
			decoder.decode(buf, decoder.getWidth()*4, Format.RGBA);
			buf.flip();
			input.close();
			glBindTexture(GL_TEXTURE_2D_ARRAY,tex.getTextureArray());
			glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA16, 32, 32, id.length+1);
			glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT);
			glPixelStorei(GL_UNPACK_ALIGNMENT,1);
			glPixelStorei(GL_PACK_ALIGNMENT,1);
			for(int i = 0; i < id.length;i++) {
				for(int j = i*tex.getTexUnitHeight()/height; j < (i*tex.getTexUnitHeight()+width*tex.getTexUnitHeight())/height;j++) 
					for(int k = i*tex.getTexUnitHeight()%width; k < i*tex.getTexUnitHeight()%width+tex.getTexUnitHeight();k++) {
//						temp.put(buf.asIntBuffer().get(j*height+k));
						try {
							temp.put(buf.get(j*width*4+k*4));
							temp.put(buf.get(j*width*4+k*4+1));
							temp.put(buf.get(j*width*4+k*4+2));
							temp.put(buf.get(j*width*4+k*4+3));
						}
						catch(Exception e) {
							e.printStackTrace();
							System.out.println(j*width*4+k*4);
						}
					}
				temp.flip();
				glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, id[i], tex.getTexUnitWidght(), tex.getTexUnitWidght(), 1, GL_RGBA, GL_UNSIGNED_BYTE, temp);
				temp.clear();
				buf.rewind();
			}
			//buf.
		//glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
			return tex;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
	
	public static Texture LoadTexture(String filename) {
		Texture tex = new Texture(filename);
		tex.setId(glGenTextures());
		int width;
		int height;
		try {
			BufferedInputStream input = new BufferedInputStream(new FileInputStream("./resources/textures/"+filename));
			PNGDecoder decoder = new PNGDecoder(input);
			tex.setHeigth(decoder.getHeight());
			tex.setWidth(decoder.getWidth());
			ByteBuffer buf = ByteBuffer.allocateDirect(4*decoder.getHeight()*decoder.getWidth());
			decoder.decode(buf, decoder.getWidth()*4, Format.RGBA);
			buf.flip();
			input.close();
			glBindTexture(GL_TEXTURE_2D,tex.getId());
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			glPixelStorei(GL_UNPACK_ALIGNMENT,1);
			glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,tex.getWidth(),tex.getHeigth(),0,GL_RGBA,GL_UNSIGNED_BYTE,buf);
			glGenerateMipmap(GL_TEXTURE_2D);
			return tex;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
	/*public static Texture LoadTexture(String filename) {
		Texture tex = new Texture(filename);
		//tex.setId(glGenTextures());
		int width;
		int height;
		try {
			tex.setId(TextureLoader.getTexture("PNG", new FileInputStream(new File("./resources/textures/"+filename))).getTextureID());
			return tex;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}*/
	
	public static String LoadShader(String fileName) {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		String currentLine = null;

		try {
			reader = new BufferedReader(new FileReader("./resources/shaders/"+fileName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("Unable to create a buffered reader");
			System.exit(1);
		}
		
		try {
			while ((currentLine = reader.readLine())!=null) {
				builder.append(currentLine).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Unable to add a line");
			System.exit(1);
		}
		return builder.toString();
	}
	
	/*public static Mesh loadMeshObj(String fileName) {
		BufferedReader reader = null;
		String currentLine = null;

		try {
			reader = new BufferedReader(new FileReader("./resources/models/"+fileName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("Unable to create a buffered reader");
			System.exit(1);
		}
		
	}*/
}
