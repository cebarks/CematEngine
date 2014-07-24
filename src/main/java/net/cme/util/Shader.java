package net.cme.util;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.BufferedReader;
import java.io.FileReader;

import net.cme.engine.CMEngine;
import net.cme.model.Model;

import org.lwjgl.LWJGLException;

public class Shader {
	private int program;

	public Shader() {
		program = glCreateProgram();

		if (program == 0) {
			CMEngine.exitOnError(1, new Exception());
		}
	}

	public void addVertexShader(String source) {
		addProgram(source, GL_VERTEX_SHADER);
	}

	public void addFragmentShader(String source) {
		addProgram(source, GL_FRAGMENT_SHADER);
	}

	public void addGeometryShader(String source) {
		addProgram(source, GL_GEOMETRY_SHADER);
	}

	public void addProgram(String source, int type) {
		int shader = glCreateShader(type);

		if (shader == 0) {
			CMEngine.LOGGER.error("Shader is null");
			CMEngine.exitOnError(1, new LWJGLException());
		}

		glShaderSource(shader, source);
		glCompileShader(shader);

		if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
			CMEngine.LOGGER.error("Could not compile shader: \n" + glGetShaderInfoLog(shader, 1024));
			CMEngine.exitOnError(1, new LWJGLException());
		}
		
		glAttachShader(shader, program);
	}

	public void compile() {
		glLinkProgram(program);
		
		if (glGetShaderi(program, GL_LINK_STATUS) == GL_FALSE) {
			CMEngine.LOGGER.error("Could not link program: \n" + glGetShaderInfoLog(program, 1024));
			CMEngine.exitOnError(1, new LWJGLException());
		}
		
		glValidateProgram(program);
		
		if (glGetShaderi(program, GL_VALIDATE_STATUS) == GL_FALSE) {
			CMEngine.LOGGER.error("Could not validate program: \n" + glGetShaderInfoLog(program, 1024));
			CMEngine.exitOnError(1, new LWJGLException());
		}
	}

	public void bindToModel(Model model) {
		model.bindShader(this);
	}
	
	public String loadShaderSource(String location) {
		StringBuilder shaderSource = new StringBuilder();
		BufferedReader shaderReader = null;

		try {
			shaderReader = new BufferedReader(new FileReader( "src/main/resources/" + location));
			String line;

			while ((line = shaderReader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}

			shaderReader.close();
		} catch (Exception e) {
			CMEngine.exitOnError(1, e);
		}
		return shaderSource.toString();
	}
}
