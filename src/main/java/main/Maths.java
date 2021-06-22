package main;

/*######################################################
#          			LUDWIG VISUALIZER     		       #  
#                                                      #  
#        Author: Scolymus                              #
#        License: CC BY-NC-SA 4.0                      #
#                                                      #
#------------------------------------------------------#*/

public class Maths {
	
	/* ********************************************************************** **
	 * 																		   *
	 * 									VECTORS					   			   *
	 *                                       						   	       *
	 * ********************************************************************** */
	
	/**
	 * 
	 * Computes the cross product of two 3-vector
	 * 
	 * @param a	First vector
	 * @param b Second vector
	 * @param res a x b
	 */
	public static void crossProduct(float a[], float b[], float res[]) {
		 
		res[0] = a[1] * b[2] - b[1] * a[2];
		res[1] = a[2] * b[0] - b[2] * a[0];
		res[2] = a[0] * b[1] - b[0] * a[1];
	}
 
	/**
	 * 
	 * Computes the dot product of two 3-vector
	 * 
	 * @param a	First vector
	 * @param b Second vector
	 * @param res a x b
	 */
	public static float dotProduct(float a[], float b[]) {
		 
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}
	
	/**
	 * 
	 * Normalizes a 3-vector
	 * 
	 * @param a Vector to normalize
	 */
	public static void normalize(float a[]) {
 
		float mag = (float) Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
 
		a[0] /= mag;
		a[1] /= mag;
		a[2] /= mag;
	}
 
	/* ********************************************************************** **
	 * 																		   *
	 * 									MATRIX					   			   *
	 *                                       						   	       *
	 * ********************************************************************** */
	
	//___________________________________________________________________________
	//
	//						PROJECTION AND MODELVIEW
	//___________________________________________________________________________
	
	/**
	 *
	 * Creates the Projection matrix.
	 * 
	 * @param fov
	 * @param ratio
	 * @param nearP
	 * @param farP
	 * @param projMatrix
	 * 
	 * @return	projMatrix
	 */
	public static float[] buildProjectionMatrix(float fov, float ratio, float nearP, 
			float farP, float[] projMatrix) {

		float f = 1.0f / (float) Math.tan(fov * (Math.PI / 360.0));

		setIdentityMatrix(projMatrix, 4);

		projMatrix[0] = f / ratio;
		projMatrix[1 * 4 + 1] = f;
		projMatrix[2 * 4 + 2] = (farP + nearP) / (nearP - farP);
		projMatrix[3 * 4 + 2] = (2.0f * farP * nearP) / (nearP - farP);
		projMatrix[2 * 4 + 3] = -1.0f;
		projMatrix[3 * 4 + 3] = 0.0f;

		return projMatrix;
	}

   /**
    * 
    * This would be the view matrix.
    * 
    * @param posX
    * @param posY
    * @param posZ
    * @param lookAtX
    * @param lookAtY
    * @param lookAtZ
    * @param viewMatrix
    * @return
    */
	public static float[] setCamera(float posX, float posY, float posZ, float lookAtX,
			float lookAtY, float lookAtZ, float[] viewMatrix, 
			float rotateX, float rotateY) {

		float[] dir = new float[3];
		float[] right = new float[3];
		float[] up = new float[3];

		up[0] = 0.0f;
		up[1] = 1.0f;
		up[2] = 0.0f;

		dir[0] = (lookAtX - posX);
		dir[1] = (lookAtY - posY);
		dir[2] = (lookAtZ - posZ);
		normalize(dir);

		crossProduct(dir, up, right);
		normalize(right);

		crossProduct(right, dir, up);
		normalize(up);

		float[] aux = new float[16];

		viewMatrix[0] = right[0];
		viewMatrix[4] = right[1];
		viewMatrix[8] = right[2];
		viewMatrix[12] = 0.0f;

		viewMatrix[1] = up[0];
		viewMatrix[5] = up[1];
		viewMatrix[9] = up[2];
		viewMatrix[13] = 0.0f;

		viewMatrix[2] = -dir[0];
		viewMatrix[6] = -dir[1];
		viewMatrix[10] = -dir[2];
		viewMatrix[14] = 0.0f;

		viewMatrix[3] = 0.0f;
		viewMatrix[7] = 0.0f;
		viewMatrix[11] = 0.0f;
		viewMatrix[15] = 1.0f;
		
		setRTMatrix(aux, rotateX, rotateY, false, -posX, -posY, -posZ);		
		multMatrix(viewMatrix, aux);
		
		return viewMatrix;
	}
	
	//___________________________________________________________________________
	//
	//							GENERAL MATRIX OPERATIONS
	//___________________________________________________________________________
	
	/**
	 * 
	 * Creates the identity matrix of size rows 
	 * @param mat	Matrix array
	 * @param size	Number of rows
	 */
	public static void setIdentityMatrix(float[] mat, int size) {
		// fill matrix with 0s
		for (int i = 0; i < mat.length; ++i)
			mat[i] = 0.0f;
 
		// fill diagonal with 1s
		for (int i = 0; i < size; ++i)
			mat[i + i * size] = 1.0f;
	}
 
	/**
	 * 
	 * Multiplies 2 matrix. Result is given at "a" matrix and follows
	 * a = a � b
	 * 
	 * @param a	First matrix and output matrix
	 * @param b	Second matrix
	 */
	public static void multMatrix(float[] a, float[] b) {
 
		float[] res = new float[16];
 
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 4; ++j) {
				res[j * 4 + i] = 0.0f;
				for (int k = 0; k < 4; ++k) {
					res[j * 4 + i] += a[k * 4 + i] * b[j * 4 + k];
				}
			}
		}
		System.arraycopy(res, 0, a, 0, 16);
	}
 
	public static float[] multMatrixf(float[] a, float[] b) {
		 
		float[] res = new float[16];
 
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 4; ++j) {
				res[j * 4 + i] = 0.0f;
				for (int k = 0; k < 4; ++k) {
					res[j * 4 + i] += a[k * 4 + i] * b[j * 4 + k];
				}
			}
		}
		//System.arraycopy(res, 0, a, 0, 16);
		return res;
	}
	
	/**
	 * 
	 * Defines a transformation matrix mat with a translation
	 * 
	 * @param mat	Matrix of translation
	 * @param x	Translate x
	 * @param y	Translate y
	 * @param z	Translate z
	 */
	public static void setTranslationMatrix(float[] mat, float x, float y, 
			float z) {
 
		setIdentityMatrix(mat, 4);
		mat[12] = x;
		mat[13] = y;
		mat[14] = z;
	}
 
	/**
	 * 
	 * Defines a transformation matrix mat with a rotation
	 * 
	 * @param mat	Matrix of rotation
	 * @param theta	Rotate theta
	 * @param phi	Rotate phi
	 * @param radians	True if both angles are in radians. False if both of 
	 * 					them are in degrees
	 */
	public static void setRotationMatrix(float[] mat, float theta, float phi, 
			boolean radians) {
		
		setIdentityMatrix(mat, 4);
		
		if (!radians){
			theta = (float) (theta*Math.PI/180.0);
			phi = (float) (phi*Math.PI/180.0);
		}
		
		mat[0] = (float) (Math.cos(theta)*Math.cos(phi));
		mat[1] = (float) (Math.cos(theta)*Math.sin(phi));
		mat[2] = (float) (-Math.sin(theta));		
		
		mat[4] = (float) (-Math.sin(phi));
		mat[5] = (float) (Math.cos(phi));
		
		mat[8] = (float) (Math.sin(theta)*Math.cos(phi));
		mat[9] = (float) (Math.sin(theta)*Math.sin(phi));
		mat[10] = (float) (Math.cos(theta));
		
		mat[15] = 1.f;		

	}
	
	/**
	 * 
	 * Defines a transformation matrix mat with a rotation around the y axis
	 * 
	 * @param mat	Matrix of rotation
	 * @param theta	Angle to rotate
	 * @param radians	True if both angles are in radians. False if both of 
	 * 					them are in degrees
	 */
	public static void setRotationMatrixy(float[] mat, float theta, boolean 
			radians) {
		
		setIdentityMatrix(mat, 4);
		
		if (!radians){
			theta = (float) (theta*Math.PI/180.0);
		}
		
		mat[0] = (float) (Math.cos(theta));
		mat[2] = (float) (-Math.sin(theta));		
		
		mat[5] = 1.f;
		
		mat[8] = (float) (Math.sin(theta));
		mat[10] = (float) (Math.cos(theta));
		
		mat[15] = 1.f;		

	}
	
	/**
	 * 
	 * Defines a transformation matrix mat with a rotation around the z axis
	 * 
	 * @param mat	Matrix of rotation
	 * @param theta	Angle to rotate
	 * @param radians	True if both angles are in radians. False if both of 
	 * 					them are in degrees
	 */
	public static void setRotationMatrixz(float[] mat, float theta, 
			boolean radians) {
		
		setIdentityMatrix(mat, 4);
		
		if (!radians){
			theta = (float) (theta*Math.PI/180.0);
		}
		
		mat[0] = (float) (Math.cos(theta));
		mat[1] = (float) (Math.sin(theta));		
		
		mat[4] = (float) (-Math.sin(theta));
		mat[5] = (float) (Math.cos(theta));		

		mat[10] = 1.f;
		
		mat[15] = 1.f;		


	}
	
	/**
	 * 
	 * Defines a transformation matrix mat with a rotation and after, 
	 * a translation
	 * 
	 * @param mat	Matrix of rotation
	 * @param theta	Rotate theta
	 * @param phi	Rotate phi
	 * @param radians	True if both angles are in radians. False if 
	 * 					both of them are in degrees
	 * @param x	Translate x
	 * @param y	Translate y
	 * @param z	Translate z
	 */
	public static void setRTMatrix(float[] mat, float theta, float phi, 
			boolean radians, float x, float y, float z) {	
		if (!radians){
			theta = (float) (theta*Math.PI/180.0);
			phi = (float) (phi*Math.PI/180.0);
		}
		
		mat[0] = (float) (Math.cos(theta)*Math.cos(phi));
		mat[1] = (float) (Math.cos(theta)*Math.sin(phi));
		mat[2] = (float) (-Math.sin(theta));		
		mat[3] = 0.f;	
		
		mat[4] = (float) (-Math.sin(phi));
		mat[5] = (float) (Math.cos(phi));
		mat[6] = 0.f;
		mat[7] = 0.f;
		
		mat[8] = (float) (Math.sin(theta)*Math.cos(phi));
		mat[9] = (float) (Math.sin(theta)*Math.sin(phi));
		mat[10] = (float) (Math.cos(theta));
		mat[11] = 0.f;
		
		mat[12] = x;
		mat[13] = y;
		mat[14] = z;
		mat[15] = 1.f;			
	}
	
}
