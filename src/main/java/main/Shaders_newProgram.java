package main;

/*######################################################
#          			LUDWIG VISUALIZER     		       #  
#                                                      #  
#        Author: Scolymus                              #
#        License: CC BY-NC-SA 4.0                      #
#                                                      #
#------------------------------------------------------#*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

public class Shaders_newProgram {
	
	enum ShaderType{ VertexShader, FragmentShader}
	enum ObjectType{ Janus, Phi_field, Vel_field}

	GL4 gl;
	int Program_ID;
	int vertexLoc, colorLoc;
	int projMatrixLoc, viewMatrixLoc, phi_max, phi_min, phi_zero, Radius, SpheresLoc;

	/* ********************************************************************** **
	 * 																		   *
	 * 							SHADER CREATION					   			   *
	 *                                       						   	       *
	 * ********************************************************************** */
	
	/**
	 *
	 * 	Creates a new Shader program
	 * 
	 * @param gl	GL4 variable where to draw
	 * @param option	false if it is for phi. True otherwise
	 * @param type	only if option=false. 0: phi 1: sphere 2: sphere_mult
	 * @return	int value to reference with shader program just created
	 * 
	 */
	public Shaders_newProgram(GL4 gl_, ObjectType option, int type) {
		// create the two shader and compile them
		int v = 0;
		int f = 0;
		gl = gl_;
		
		if (option == ObjectType.Janus){
			v = newShaderFromCurrentClass(gl, "vertex.shader", ShaderType.VertexShader);
			f = newShaderFromCurrentClass(gl, "fragment.shader", ShaderType.FragmentShader);
		}else if (option == ObjectType.Phi_field){		
			if (type == 0) {
				v = newShaderFromCurrentClass(gl, "phi_vertex.shader", ShaderType.VertexShader);
			} else if (type == 1) {
				v = newShaderFromCurrentClass(gl, "phi_vertex_sphere.shader", ShaderType.VertexShader);
			} else if (type == 2) {
				v = newShaderFromCurrentClass(gl, "phi_vertex_sphere_mult.shader", ShaderType.VertexShader);
			}else if (type == 100) {
				v = newShaderFromCurrentClass(gl, "phi_vertex.shader", ShaderType.VertexShader);
			}
			if (type <100) {
				f = newShaderFromCurrentClass(gl, "phi_fragment.shader", ShaderType.FragmentShader);
			}else {
				f = newShaderFromCurrentClass(gl, "psi_fragment.shader", ShaderType.FragmentShader);
			}
		}else if (option == ObjectType.Vel_field){		
			v = newShaderFromCurrentClass(gl, "vertex.shader", ShaderType.VertexShader);
			f = newShaderFromCurrentClass(gl, "fragment.shader", ShaderType.FragmentShader);
		}

		System.out.println(getShaderInfoLog(gl, v));
		System.out.println(getShaderInfoLog(gl, f));
 
		Program_ID = createProgram(gl, v, f);
 
		gl.glBindFragDataLocation(Program_ID, 0, "outColor");
		printProgramInfoLog(gl, Program_ID);
 
		vertexLoc = gl.glGetAttribLocation(Program_ID, "position");
		colorLoc = gl.glGetAttribLocation(Program_ID, "color");
 
		projMatrixLoc = gl.glGetUniformLocation(Program_ID, "projMatrix");
		viewMatrixLoc = gl.glGetUniformLocation(Program_ID, "viewMatrix");
 
		if (option == ObjectType.Phi_field) {
			phi_max = gl.glGetUniformLocation(Program_ID, "phi_max");
			if (type>99) {
				phi_min = gl.glGetUniformLocation(Program_ID, "phi_min");
				phi_zero = gl.glGetUniformLocation(Program_ID, "phi_zero");
			}
			Radius = gl.glGetUniformLocation(Program_ID, "Radius");
			SpheresLoc = gl.glGetUniformLocation(Program_ID, "pos");
		}
		
		setupBuffers(gl,option);
	}	
	
	/**
	 *
	 * 	Compiles the Shader program
	 * 
	 * @param gl	GL4 variable where to draw
	 * @param fileName	Route to shader program file.
	 * @return	type	VertexShader or FragmentShader (0 or 1)
	 * 
	 */
	private int newShaderFromCurrentClass(GL4 gl, String fileName, ShaderType type){
		// load the source
		String shaderSource = this.loadStringFileFromCurrentPackage( fileName);
		// define the shaper type from the enum
		int shaderType = type==ShaderType.VertexShader?GL4.GL_VERTEX_SHADER:GL4.GL_FRAGMENT_SHADER;
		// create the shader id
		int id = gl.glCreateShader(shaderType);
		//  link the id and the source
		gl.glShaderSource(id, 1, new String[] { shaderSource }, null);
		//compile the shader
		gl.glCompileShader(id);
 
		return id;
	}
 	
 	/**
 	 * 
 	 * 	Loads the file where the shader is
 	 * 
 	 * @param fileName	File path to shader program.
 	 * @return	All the text in the shader program
 	 * 
 	 */
	private String loadStringFileFromCurrentPackage( String fileName){
		//InputStream stream = this.getClass().getResourceAsStream(fileName);
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(fileName);	
		//Notice: without maven files were at the same folder as this .java. I didn't have to include the
		//.getClassLoader() to work!

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		// allocate a string builder to add line per line 
		StringBuilder strBuilder = new StringBuilder();
 
		try {
			String line = reader.readLine();
			// get text from file, line per line
			while(line != null){
				strBuilder.append(line + "\n");
				line = reader.readLine();	
			}
			// close resources
			reader.close();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		return strBuilder.toString();
	}
 	
	/**
	 *
	 * 	Attaches the vertexes and colors and links the Shader program
	 * 
	 * @param gl	GL4 variable where to draw
	 * @param vertexShaderId	ID for vertexShader.
	 * @param	fragmentShaderId	ID for fragmentShaderId.
	 * @return	The programID
	 * 
	 */
	private int createProgram(GL4 gl, int vertexShaderId, int fragmentShaderId) {
		// generate the id of the program
		int programId = gl.glCreateProgram();
		// attach the two shader
		gl.glAttachShader(programId, vertexShaderId);
		gl.glAttachShader(programId, fragmentShaderId);
		// link them
		gl.glLinkProgram(programId);
 
		return programId;
	}
 
	
	
	/* ********************************************************************** **
	 * 																		   *
	 * 							SHADER LOGS						   			   *
	 *                                       						   	       *
	 * ********************************************************************** */
	
	/**
	 *
	 * 	Retrieves the info log for the program
	 * 
	 * @param gl	GL4 variable where to draw
	 * @param obj	PROGRAM_ID.
	 * @return	The log message
	 * 
	 */
	private String printProgramInfoLog(GL4 gl, int obj) {
		// get the GL info log
		final int logLen = getProgramParameter(gl, obj, GL4.GL_INFO_LOG_LENGTH);
		if (logLen <= 0)
			return "";
 
		// Get the log
		final int[] retLength = new int[1];
		final byte[] bytes = new byte[logLen + 1];
		gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0);
		final String logMessage = new String(bytes);
 
		return logMessage;
	}

	 
	/** 
	 *
	 * 	Retrieves the info log for the shader
	 * 
	 * @param gl	GL4 variable where to draw
	 * @param obj	parameter_ID.
	 * @return	The log message
	 * 
	 */
	public String getShaderInfoLog(GL4 gl, int obj) {
		// Otherwise, we'll get the GL info log
		final int logLen = getShaderParameter(gl, obj, GL4.GL_INFO_LOG_LENGTH);
		if (logLen <= 0)
			return "";
	 
		// Get the log
		final int[] retLength = new int[1];
		final byte[] bytes = new byte[logLen + 1];
		gl.glGetShaderInfoLog(obj, logLen, retLength, 0, bytes, 0);
		final String logMessage = new String(bytes);
	 
		return String.format("ShaderLog: %s", logMessage);
	}
	 
	
	/**
	 *
	 * 	Gets a program parameter value
	 * 
	 * @param gl	GL4 variable where to draw
	 * @param obj	PROGRAM_ID.
	 * @param paramName	ID for the parameter to look for.
	 * @return	The parameter value
	 * 
	 */
	private int getProgramParameter(GL4 gl, int obj, int paramName) {
		final int params[] = new int[1];
		gl.glGetProgramiv(obj, paramName, params, 0);
		return params[0];
	}

	/**
	 *
	 * 	Gets a shader parameter value. See 'glGetShaderiv'
	 * 
	 * @param gl	GL4 variable where to draw
	 * @param obj	parameter_ID.
	 * @param paramName	ID for the parameter to look for.
	 * @return	The parameter value
	 * 
	 */
	private int getShaderParameter(GL4 gl, int obj, int paramName) {
		final int params[] = new int[1];
		gl.glGetShaderiv(obj, paramName, params, 0);
		return params[0];
	}
	
		
	
	/* ********************************************************************** **
	 * 																		   *
	 * 							BUFFER OBJECTS					   			   *
	 *                                       						   	       *
	 * ********************************************************************** */
	
	/**
	 *
	 * 	Creates all the buffer objects
	 * 
	 * @param gl	GL4 variable where to draw
	 * 
	 */
	private void setupBuffers(GL4 gl, ObjectType option) {

		if (option == ObjectType.Janus){
			// generate the IDs
			main.triangleVAO = this.generateVAOId(gl);
			main.axisVAO = this.generateVAOId(gl);
			main.wallsxVAO = this.generateVAOId(gl);
			main.wallsyVAO = this.generateVAOId(gl);
			main.wallszVAO = this.generateVAOId(gl);

			// create the buffer and link the data with the location inside the vertex shader
			this.newFloatVertexAndColorBuffers(gl, main.triangleVAO, 
					main.janusv, main.janusc, this.vertexLoc, this.colorLoc, true);
			this.newFloatVertexAndColorBuffers(gl, main.axisVAO,
					main.verticesAxis, main.colorAxis, this.vertexLoc, this.colorLoc, true);
			this.newFloatVertexAndColorBuffers(gl, main.wallsxVAO,
					main.wallsv[0], main.wallsc, this.vertexLoc, this.colorLoc, true);
			this.newFloatVertexAndColorBuffers(gl, main.wallsyVAO,
					main.wallsv[1], main.wallsc, this.vertexLoc, this.colorLoc, true);
			this.newFloatVertexAndColorBuffers(gl, main.wallszVAO,
					main.wallsv[2], main.wallsc, this.vertexLoc, this.colorLoc, true);

		}else if (option == ObjectType.Phi_field){		
			main.boxVAO = this.generateVAOId(gl);
		
			this.newFloatVertexAndColorBuffers(gl, main.boxVAO,
				main.boxAxis, main.boxCol_t[0], this.vertexLoc, this.colorLoc, false);
			
		}else if (option == ObjectType.Vel_field){		
			main.boxVAO = this.generateVAOId(gl);
		
			this.newFloatVertexAndColorBuffers(gl, main.boxVAO,
				main.boxAxis, main.boxCol_t_vel[0], this.vertexLoc, this.colorLoc, true);
		}
		
	}
 
	private int generateVAOId(GL4 gl) {
		// allocate an array of one element in order to strore 
		// the generated id
		int[] idArray = new int[1];
		// let's generate
		gl.glGenVertexArrays(1, idArray, 0);
		// return the id
		return idArray[0];
	}
	
	private void newFloatVertexAndColorBuffers(GL4 gl, int vaoId, float[] verticesArray, float[] colorArray, int verticeLoc, int colorLoc, boolean option){
		// bind the correct VAO id
		gl.glBindVertexArray( vaoId);
		// Generate two slots for the vertex and color buffers
		int vertexBufferId = this.generateBufferId(gl);
		int colorBufferId = this.generateBufferId(gl);
 
		if (!option) main.colorBox = colorBufferId;
		
		// bind the two buffer
		this.bindBuffer(gl, vertexBufferId, verticesArray, verticeLoc, true);
		this.bindBuffer(gl, colorBufferId, colorArray, colorLoc, option);
	}
 
	private void bindBuffer(GL4 gl, int bufferId, float[] dataArray, int dataLoc, boolean option){
		// bind buffer for vertices and copy data into buffer
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, bufferId);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, dataArray.length * Float.SIZE / 8,
				Buffers.newDirectFloatBuffer(dataArray), GL4.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(dataLoc);
		if (option == true) {
			gl.glVertexAttribPointer(dataLoc, 4, GL4.GL_FLOAT, false, 0, 0);
		}else{
			gl.glVertexAttribPointer(dataLoc, 1, GL4.GL_FLOAT, false, 0, 0);
		}
	}

	private int generateBufferId(GL4 gl) {
		// allocate an array of one element in order to strore 
		// the generated id
		int[] idArray = new int[1];
		// let's generate
		gl.glGenBuffers( 1, idArray, 0);
 
		// return the id
		return idArray[0];
	}
 

}
