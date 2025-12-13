///////////////////////////////////////////////////////////////////////////////
// scenemanager.h
// ============
// manage the preparing and rendering of 3D scenes - textures, materials, lighting
//
//  AUTHOR: Brian Battersby - SNHU Instructor / Computer Science
//	Created for CS-330-Computational Graphics and Visualization, Nov. 1st, 2023
///////////////////////////////////////////////////////////////////////////////

#pragma once

#include "ShaderManager.h"
#include "ShapeMeshes.h"
#include "Model.h"
#include <string>
#include <vector>

/***********************************************************
 *	Added for Scene Graph needed for CS-499 - Algorithms and 
	Data Structures 
 * 
 *	Struct Node
 *
 *  Create a structure for a scene graph node to hold
 *	hierarchy information about objects in the scene.
 ***********************************************************/
struct SceneNode
{
	glm::vec3 scale = { 1,1,1 };
	glm::vec3 rotation = { 0,0,0 }; // degrees
	glm::vec3 position = { 0,0,0 };

	std::string textureName;
	std::string materialName;
	enum MeshType { None, Plane, Box, Cylinder, Prism, TaperedCylinder, ModelMesh } meshType = None;

	Model* model = nullptr;

	std::vector<SceneNode*> children;
};

/***********************************************************
 *  SceneManager
 *
 *  This class contains the code for preparing and rendering
 *  3D scenes, including the shader settings.
 ***********************************************************/
class SceneManager
{
public:
	// constructor
	SceneManager(ShaderManager *pShaderManager);
	// destructor
	~SceneManager();

	struct TEXTURE_INFO
	{
		std::string tag ="";
		uint32_t ID = 0;
	};

	struct OBJECT_MATERIAL
	{
		glm::vec3 diffuseColor = glm::vec3(1.0f, 1.0f, 1.0f);
		glm::vec3 specularColor = glm::vec3(1.0f, 1.0f, 1.0f);
		float shininess = 32.0f;
		std::string tag = "";
	};

private:
	// pointer to shader manager object
	ShaderManager* m_pShaderManager;
	// pointer to basic shapes object
	ShapeMeshes* m_basicMeshes;
	// pointer to the root of the scene graph
	SceneNode* m_sceneParent = nullptr;
	// total number of loaded textures
	int m_loadedTextures;
	// loaded textures info
	static const int MAX_TEXTURES = 16;
	TEXTURE_INFO m_textureIDs[MAX_TEXTURES];
	// defined object materials
	std::vector<OBJECT_MATERIAL> m_objectMaterials;

	// load texture images and convert to OpenGL texture data
	bool CreateGLTexture(const char* filename, std::string tag);
	// bind loaded OpenGL textures to slots in memory
	void BindGLTextures();
	// free the loaded OpenGL textures
	void DestroyGLTextures();
	// find a loaded texture by tag
	int FindTextureID(std::string tag);
	int FindTextureSlot(std::string tag);
	// find a defined material by tag
	bool FindMaterial(std::string tag, OBJECT_MATERIAL& material);

	// set the transformation values 
	// into the transform buffer
	void SetTransformations(
		glm::vec3 scaleXYZ,
		float XrotationDegrees,
		float YrotationDegrees,
		float ZrotationDegrees,
		glm::vec3 positionXYZ);

	// set the color values into the shader
	void SetShaderColor(
		float redColorValue,
		float greenColorValue,
		float blueColorValue,
		float alphaValue);

	// set the texture data into the shader
	void SetShaderTexture(
		std::string textureTag);

	// set the UV scale for the texture mapping
	void SetTextureUVScale(
		float u, float v);

	// set the object material into the shader
	void SetShaderMaterial(
		std::string materialTag);

public:

	// The following methods are for the students to 
	// customize for their own 3D scene
	void PrepareScene();
	void DrawNode(SceneNode* node, glm::mat4 parentTransform);
	void RenderScene();

	// loads textures from image files
	void LoadSceneTextures();

	// pre-set light sources for 3D scene
	void SetupSceneLights();
	// pre-define the object materials for lighting
	void DefineObjectMaterials();

};