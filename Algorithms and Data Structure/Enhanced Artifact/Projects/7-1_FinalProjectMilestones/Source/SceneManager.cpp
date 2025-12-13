///////////////////////////////////////////////////////////////////////////////
// scenemanager.cpp
// ============
// manage the preparing and rendering of 3D scenes - textures, materials, lighting
//
//  AUTHOR: Brian Battersby - SNHU Instructor / Computer Science
//	Created for CS-330-Computational Graphics and Visualization, Nov. 1st, 2023
///////////////////////////////////////////////////////////////////////////////

// 3D Planchette Model Attribution:
// Model by  Ludwig Battran, obtained from sketchfab.com
// License: Creative Commons Attribution ( https://creativecommons.org/licenses/by/4.0/ ) 
// This project uses the model in accordance with the license terms.


#include "SceneManager.h"
#include "stb_image.h"
#include "tiny_gltf.h"
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtx/matrix_decompose.hpp>
#include <glm/gtc/type_ptr.hpp>
#include <cmath>


// declaration of global variables
namespace
{
	const char* g_ModelName = "model";
	const char* g_ColorValueName = "objectColor";
	const char* g_TextureValueName = "objectTexture";
	const char* g_UseTextureName = "bUseTexture";
	const char* g_UseLightingName = "bUseLighting";
}

/***********************************************************
 *  SceneManager()
 *
 *  The constructor for the class
 ***********************************************************/
SceneManager::SceneManager(ShaderManager* pShaderManager)
{
	m_pShaderManager = pShaderManager;
	m_basicMeshes = new ShapeMeshes();
	m_loadedTextures = 0;
}

/***********************************************************
 *  ~SceneManager()
 *
 *  The destructor for the class
 ***********************************************************/
SceneManager::~SceneManager()
{
	m_pShaderManager = NULL;
	delete m_basicMeshes;
	m_basicMeshes = NULL;
}

/***********************************************************
 *  CreateGLTexture()
 *
 *  This method is used for loading textures from image files,
 *  configuring the texture mapping parameters in OpenGL,
 *  generating the mipmaps, and loading the read texture into
 *  the next available texture slot in memory.
 ***********************************************************/
bool SceneManager::CreateGLTexture(const char* filename, std::string tag)
{

	// if the maximum number of textures has already been loaded
	if (m_loadedTextures >= MAX_TEXTURES)
	{
		std::cout << "ERROR: Texture limit (16) reached. Cannot load: "
			<< filename << "\n";
		return false;
	}

	int width = 0;
	int height = 0;
	int colorChannels = 0;
	GLuint textureID = 0;

	// indicate to always flip images vertically when loaded
	stbi_set_flip_vertically_on_load(true);

	// try to parse the image data from the specified image file
	unsigned char* image = stbi_load(
		filename,
		&width,
		&height,
		&colorChannels,
		0);

	// if the image was successfully read from the image file
	if (image)
	{
		std::cout << "Successfully loaded image:" << filename << ", width:" << width << ", height:" << height << ", channels:" << colorChannels << std::endl;

		glGenTextures(1, &textureID);
		glBindTexture(GL_TEXTURE_2D, textureID);

		// set the texture wrapping parameters
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		// set texture filtering parameters
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		// if the loaded image is in RGB format
		if (colorChannels == 3)
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, image);
		// if the loaded image is in RGBA format - it supports transparency
		else if (colorChannels == 4)
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
		else
		{
			std::cout << "Not implemented to handle image with " << colorChannels << " channels" << std::endl;
			return false;
		}

		// generate the texture mipmaps for mapping textures to lower resolutions
		glGenerateMipmap(GL_TEXTURE_2D);

		// free the image data from local memory
		stbi_image_free(image);
		glBindTexture(GL_TEXTURE_2D, 0); // Unbind the texture

		// register the loaded texture and associate it with the special tag string
		m_textureIDs[m_loadedTextures].ID = textureID;
		m_textureIDs[m_loadedTextures].tag = tag;
		m_loadedTextures++;

		return true;
	}

	std::cout << "Could not load image:" << filename << std::endl;

	// Error loading the image
	return false;
}

/***********************************************************
 *  BindGLTextures()
 *
 *  This method is used for binding the loaded textures to
 *  OpenGL texture memory slots.  There are up to 16 slots.
 ***********************************************************/
void SceneManager::BindGLTextures()
{
	for (int i = 0; i < m_loadedTextures; i++)
	{
		// bind textures on corresponding texture units
		glActiveTexture(GL_TEXTURE0 + i);
		glBindTexture(GL_TEXTURE_2D, m_textureIDs[i].ID);
	}
}

/***********************************************************
 *  DestroyGLTextures()
 *
 *  This method is used for freeing the memory in all the
 *  used texture memory slots.
 ***********************************************************/
void SceneManager::DestroyGLTextures()
{
	for (int i = 0; i < m_loadedTextures; i++)
	{
		glDeleteTextures(1, &m_textureIDs[i].ID);
	}
}

/***********************************************************
 *  FindTextureID()
 *
 *  This method is used for getting an ID for the previously
 *  loaded texture bitmap associated with the passed in tag.
 ***********************************************************/
int SceneManager::FindTextureID(std::string tag)
{
	int textureID = -1;
	int index = 0;
	bool bFound = false;

	while ((index < m_loadedTextures) && (bFound == false))
	{
		if (m_textureIDs[index].tag.compare(tag) == 0)
		{
			textureID = m_textureIDs[index].ID;
			bFound = true;
		}
		else
			index++;
	}

	return(textureID);
}

/***********************************************************
 *  FindTextureSlot()
 *
 *  This method is used for getting a slot index for the previously
 *  loaded texture bitmap associated with the passed in tag.
 ***********************************************************/
int SceneManager::FindTextureSlot(std::string tag)
{
	int textureSlot = -1;
	int index = 0;
	bool bFound = false;

	while ((index < m_loadedTextures) && (bFound == false))
	{
		if (m_textureIDs[index].tag.compare(tag) == 0)
		{
			textureSlot = index;
			bFound = true;
		}
		else
			index++;
	}

	return(textureSlot);
}

/***********************************************************
 *  FindMaterial()
 *
 *  This method is used for getting a material from the previously
 *  defined materials list that is associated with the passed in tag.
 ***********************************************************/
bool SceneManager::FindMaterial(std::string tag, OBJECT_MATERIAL& material)
{
	if (m_objectMaterials.size() == 0)
	{
		return(false);
	}

	int index = 0;
	bool bFound = false;
	while ((static_cast<size_t>(index) < m_objectMaterials.size()) && (bFound == false))
	{
		if (m_objectMaterials[index].tag.compare(tag) == 0)
		{
			bFound = true;
			material.diffuseColor = m_objectMaterials[index].diffuseColor;
			material.specularColor = m_objectMaterials[index].specularColor;
			material.shininess = m_objectMaterials[index].shininess;
		}
		else
		{
	}
		{
			index++;
		}
	}

	return(true);
}

/***********************************************************
 *  SetTransformations()
 *
 *  This method is used for setting the transform buffer
 *  using the passed in transformation values.
 ***********************************************************/
void SceneManager::SetTransformations(
	glm::vec3 scaleXYZ,
	float XrotationDegrees,
	float YrotationDegrees,
	float ZrotationDegrees,
	glm::vec3 positionXYZ)
{
	// variables for this method
	glm::mat4 modelView;
	glm::mat4 scale;
	glm::mat4 rotationX;
	glm::mat4 rotationY;
	glm::mat4 rotationZ;
	glm::mat4 translation;

	// set the scale value in the transform buffer
	scale = glm::scale(scaleXYZ);
	// set the rotation values in the transform buffer
	rotationX = glm::rotate(glm::radians(XrotationDegrees), glm::vec3(1.0f, 0.0f, 0.0f));
	rotationY = glm::rotate(glm::radians(YrotationDegrees), glm::vec3(0.0f, 1.0f, 0.0f));
	rotationZ = glm::rotate(glm::radians(ZrotationDegrees), glm::vec3(0.0f, 0.0f, 1.0f));
	// set the translation value in the transform buffer
	translation = glm::translate(positionXYZ);

	modelView = translation * rotationZ * rotationY * rotationX * scale;

	if (NULL != m_pShaderManager)
	{
		m_pShaderManager->setMat4Value(g_ModelName, modelView);
	}
}

/***********************************************************
 *  SetShaderColor()
 *
 *  This method is used for setting the passed in color
 *  into the shader for the next draw command
 ***********************************************************/
void SceneManager::SetShaderColor(
	float redColorValue,
	float greenColorValue,
	float blueColorValue,
	float alphaValue)
{
	// variables for this method
	glm::vec4 currentColor;

	currentColor.r = redColorValue;
	currentColor.g = greenColorValue;
	currentColor.b = blueColorValue;
	currentColor.a = alphaValue;

	if (NULL != m_pShaderManager)
	{
		m_pShaderManager->setIntValue(g_UseTextureName, false);
		m_pShaderManager->setVec4Value(g_ColorValueName, currentColor);
	}
}

/***********************************************************
 *  SetShaderTexture()
 *
 *  This method is used for setting the texture data
 *  associated with the passed in ID into the shader.
 ***********************************************************/
void SceneManager::SetShaderTexture(
	std::string textureTag)
{
	if (NULL != m_pShaderManager)
	{
		m_pShaderManager->setIntValue(g_UseTextureName, true);

		int textureID = -1;
		textureID = FindTextureSlot(textureTag);
		m_pShaderManager->setSampler2DValue(g_TextureValueName, textureID);
	}
}

/***********************************************************
 *  SetTextureUVScale()
 *
 *  This method is used for setting the texture UV scale
 *  values into the shader.
 ***********************************************************/
void SceneManager::SetTextureUVScale(float u, float v)
{
	if (NULL != m_pShaderManager)
	{
		m_pShaderManager->setVec2Value("UVscale", glm::vec2(u, v));
	}
}

/***********************************************************
  *  LoadSceneTextures()
  *
  *  This method is used for preparing the 3D scene by loading
  *  the shapes, textures in memory to support the 3D scene
  *  rendering
  ***********************************************************/
void SceneManager::LoadSceneTextures()
{
	struct TexDef {
		const char* file;
		const char* tag;
	};

	// list of textures to load
	TexDef textures[] = {
		{ "textures/zion.png",                "dark-wood" },
		{ "textures/dark-cloth-1024x1024.jpg","fabric"    },
		{ "textures/board.jpg",               "board"     },
		{ "textures/card.jpg",                "card"      },
		{ "textures/candle.jpg",              "candle"    },
		{ "textures/11.jpg",                  "stick"     },
		{ "textures/dirt.png",				  "dirt"      }
	};

	// load each texture in the list
	for (auto& t : textures)
	{
		if (!CreateGLTexture(t.file, t.tag))
			std::cout << "Failed to load texture: " << t.file << "\n";
	}

	SetTextureUVScale(4.0f, 4.0f);

	BindGLTextures();
}


/***********************************************************
 *  SetShaderMaterial()
 *
 *  This method is used for passing the material values
 *  into the shader.
 ***********************************************************/
void SceneManager::SetShaderMaterial(
	std::string materialTag)
{
	if (m_objectMaterials.size() > 0)
	{
		OBJECT_MATERIAL material;
		bool bReturn = false;

		bReturn = FindMaterial(materialTag, material);
		if (bReturn == true)
		{
			m_pShaderManager->setVec3Value("material.diffuseColor", material.diffuseColor);
			m_pShaderManager->setVec3Value("material.specularColor", material.specularColor);
			m_pShaderManager->setFloatValue("material.shininess", material.shininess);
		}
	}
}

/**************************************************************/
/*** STUDENTS CAN MODIFY the code in the methods BELOW for  ***/
/*** preparing and rendering their own 3D replicated scenes.***/
/*** Please refer to the code in the OpenGL sample project  ***/
/*** for assistance.                                        ***/
/**************************************************************/
void SceneManager::DefineObjectMaterials()
{
	/*** STUDENTS - add the code BELOW for defining object materials. ***/
	/*** There is no limit to the number of object materials that can ***/
	/*** be defined. Refer to the code in the OpenGL Sample for help  ***/

	// Define material for the objects
	OBJECT_MATERIAL woodMaterial;
	woodMaterial.diffuseColor = glm::vec3(0.3f, 0.2f, 0.1f);
	woodMaterial.specularColor = glm::vec3(0.1f, 0.1f, 0.1f);
	woodMaterial.shininess = (0.3f);
	woodMaterial.tag = "wood";
	m_objectMaterials.push_back(woodMaterial);

	OBJECT_MATERIAL clothMaterial;
	clothMaterial.diffuseColor = glm::vec3(0.02f, 0.02f, 0.02f);
	clothMaterial.specularColor = glm::vec3(0.0f, 0.0f, 0.0f); 
	clothMaterial.shininess = 1.0f;
	clothMaterial.tag = "cloth";
	m_objectMaterials.push_back(clothMaterial);

	OBJECT_MATERIAL glassMaterial; 
	glassMaterial.diffuseColor = glm::vec3(0.3f, 0.3f, 0.3f); 
	glassMaterial.specularColor = glm::vec3(0.6f, 0.6f, 0.6f); 
	glassMaterial.shininess = 85.0; 
	glassMaterial.tag = "glass"; 
	m_objectMaterials.push_back(glassMaterial);


	OBJECT_MATERIAL goldMaterial;  
	goldMaterial.diffuseColor = glm::vec3(0.3f, 0.3f, 0.2f); 
	goldMaterial.specularColor = glm::vec3(0.6f, 0.5f, 0.4f); 
	goldMaterial.shininess = 22.0; 
	goldMaterial.tag = "gold";
	m_objectMaterials.push_back(goldMaterial);

}

void SceneManager::SetupSceneLights()
{
	// this line of code is NEEDED for telling the shaders to render 
	// the 3D scene with custom lighting, if no light sources have
	// been added then the display window will be black - to use the 
	// default OpenGL lighting then comment out the following line
	m_pShaderManager->setBoolValue(g_UseLightingName, true);

	/*** STUDENTS - add the code BELOW for setting up light sources ***/
	/*** Up to four light sources can be defined. Refer to the code ***/
	/*** in the OpenGL Sample for help                              ***/

	
	// Directional Light (Intense Blue Moonlight)
	m_pShaderManager->setVec3Value("directionalLight.direction", 0.0f, 0.0f, 0.0f);
	m_pShaderManager->setVec3Value("directionalLight.ambient", 0.15f, 0.2f, 0.3f);
	m_pShaderManager->setVec3Value("directionalLight.diffuse", 0.4f, 0.5f, 0.9f);
	m_pShaderManager->setVec3Value("directionalLight.specular", 0.6f, 0.8f, 1.0f);
	m_pShaderManager->setBoolValue("directionalLight.bActive", true);

	// Point Light 1 
	m_pShaderManager->setVec3Value("pointLights[0].position", 0.0f, 5.0f, 15.0f);
	m_pShaderManager->setVec3Value("pointLights[0].ambient", 0.03f, 0.02f, 0.01f);
	m_pShaderManager->setVec3Value("pointLights[0].diffuse", 0.9f, 0.7f, 0.2f);
	m_pShaderManager->setVec3Value("pointLights[0].specular", 0.8f, 0.6f, 0.2f);
	m_pShaderManager->setBoolValue("pointLights[0].bActive", true);

	// Point Light 2 
	m_pShaderManager->setVec3Value("pointLights[1].position", 0.0f, 0.0f, 0.0f);
	m_pShaderManager->setVec3Value("pointLights[1].ambient", 0.0f, 0.0f, 0.0f);
	m_pShaderManager->setVec3Value("pointLights[1].diffuse", 0.3f, 0.9f, 1.8f);
	m_pShaderManager->setVec3Value("pointLights[1].specular", 0.3f, 0.9f, 1.8f);
	m_pShaderManager->setBoolValue("pointLights[1].bActive", true);

}
/***********************************************************
 *  PrepareScene()
 *
 *  This method is used for preparing the 3D scene by loading
 *  the shapes, textures in memory to support the 3D scene 
 *  rendering
 ***********************************************************/
void SceneManager::PrepareScene()
{
	// Prepares textures, materials, lights, meshes
	LoadSceneTextures();
	DefineObjectMaterials();
	SetupSceneLights();
	m_basicMeshes->LoadPlaneMesh();
	m_basicMeshes->LoadPrismMesh();
	m_basicMeshes->LoadCylinderMesh();
	m_basicMeshes->LoadBoxMesh();
	m_basicMeshes->LoadTaperedCylinderMesh();
	

	// Parent node for everything on the table
	SceneNode* tableParent = new SceneNode();
	tableParent->position = glm::vec3(0, 0, 0);

	// Surface (child of tableParent)
	SceneNode* surface = new SceneNode();
	surface->meshType = SceneNode::Plane;
	surface->scale = glm::vec3(9.0f, 1.0f, 9.0f);
	surface->position = glm::vec3(0, 0, 0);
	surface->textureName = "fabric";
	surface->materialName = "cloth";
	tableParent->children.push_back(surface);

	// Ouija board (child of surface)
	SceneNode* board = new SceneNode();
	board->meshType = SceneNode::Box;
	board->scale = glm::vec3(1.2f, 0.1f, 0.7f);
	board->position = glm::vec3(0.0f, 0.1f, 0.2f);
	board->textureName = "board";
	board->materialName = "wood";
	surface->children.push_back(board);

	// Planchette (child of board)
	Model* planchetteModel = new Model();
	// Load the planchette model from file
	planchetteModel->LoadFromFile("models/planchette.glb");
	SceneNode* planchette = new SceneNode();
	planchette->meshType = SceneNode::ModelMesh;
	planchette->model = planchetteModel;
	planchette->scale = glm::vec3(0.01f, 1.0f, 0.01f);;
	planchette->position = glm::vec3(0.2f, -2.0f, 0.65f);
	planchette->rotation = glm::vec3(0.0f, 120.0f, 0.0f);
	planchette->textureName = "dark-wood";
	planchette->materialName = "wood";
	board->children.push_back(planchette);
	
	// Candle (child of surface)
	SceneNode* candle = new SceneNode();
	candle->meshType = SceneNode::Cylinder;
	candle->scale = glm::vec3(0.1f, 3.0f, 0.1f);
	candle->position = glm::vec3(-0.6f, 0.05f, -0.4f);
	candle->textureName = "candle";
	candle->materialName = "glass";
	surface->children.push_back(candle);

	// Tarot Box (child of surface)
	SceneNode* tarotBox = new SceneNode();
	tarotBox->meshType = SceneNode::Box;
	tarotBox->scale = glm::vec3(0.3f, 1.0f, 0.1875f);
	tarotBox->position = glm::vec3(-0.8f, 0.55f, 0.2f);
	tarotBox->rotation = glm::vec3(0.0f, 90.0f, 0.0f);
	tarotBox->textureName = "card";
	tarotBox->materialName = "wood";
	surface->children.push_back(tarotBox);

	// Incense Stick 1 (child of surface)
	SceneNode* incense1 = new SceneNode();
	incense1->meshType = SceneNode::TaperedCylinder;
	incense1->scale = glm::vec3(0.005f, 0.5f, 0.05f);
	incense1->position = glm::vec3(0.8f, 0.1f, -0.02f);
	incense1->rotation = glm::vec3(90.0f, 0.0f, 90.0f);
	incense1->textureName = "dirt";
	incense1->materialName = "wood";
	surface->children.push_back(incense1);

	// Incense Stick 2 
	SceneNode* incense2 = new SceneNode();
	incense2->meshType = SceneNode::TaperedCylinder;
	incense2->scale = glm::vec3(0.005f, 0.5f, 0.05f);
	incense2->position = glm::vec3(0.85f, 0.1f, -0.02f);
	incense2->rotation = glm::vec3(90.0f, 0.0f, 90.0f);
	incense2->textureName = "dirt";
	incense2->materialName = "wood";
	surface->children.push_back(incense2);

	// Set the scene parent
	m_sceneParent = tableParent;
}


void SceneManager::DrawNode(SceneNode* node, glm::mat4 parentTransform)
{
	glm::mat4 model = parentTransform;

	// Apply this node's transformations
	model = glm::translate(model, node->position);
	model = glm::rotate(model, glm::radians(node->rotation.x), glm::vec3(1, 0, 0));
	model = glm::rotate(model, glm::radians(node->rotation.y), glm::vec3(0, 1, 0));
	model = glm::rotate(model, glm::radians(node->rotation.z), glm::vec3(0, 0, 1));
	model = glm::scale(model, node->scale);

	// Extract position
	glm::vec3 pos = glm::vec3(model[3]);

	// Extract scale
	glm::vec3 scale;
	scale.x = glm::length(glm::vec3(model[0]));
	scale.y = glm::length(glm::vec3(model[1]));
	scale.z = glm::length(glm::vec3(model[2]));

	// Extract rotation
	float rotX, rotY, rotZ;

	// Remove scaling from rotation matrix
	glm::mat3 rotMat;
	rotMat[0] = glm::vec3(model[0]) / scale.x;
	rotMat[1] = glm::vec3(model[1]) / scale.y;
	rotMat[2] = glm::vec3(model[2]) / scale.z;

	rotY = glm::degrees(atan2(-rotMat[2][0], rotMat[0][0]));
	rotX = glm::degrees(asin(rotMat[1][0]));
	rotZ = glm::degrees(atan2(-rotMat[1][2], rotMat[1][1]));

	// Call SetTransformations
	SetTransformations(
		scale,
		rotX,
		rotY,
		rotZ,
		pos
	);


	// Apply texture/material
	SetShaderTexture(node->textureName);
	SetShaderMaterial(node->materialName);

	// Draw the mesh
	switch (node->meshType)
	{
	case SceneNode::Plane:     m_basicMeshes->DrawPlaneMesh(); break;
	case SceneNode::Box:       m_basicMeshes->DrawBoxMesh(); break;
	case SceneNode::Cylinder:  m_basicMeshes->DrawCylinderMesh(); break;
	case SceneNode::Prism:     m_basicMeshes->DrawPrismMesh(); break;
	case SceneNode::TaperedCylinder: m_basicMeshes->DrawTaperedCylinderMesh(); break;
	
	// Case for loading model mesh
	case SceneNode::ModelMesh:
		if (node->model != nullptr)
			node->model->Draw();
		break;
	
	// Default case - draw nothing
	case SceneNode::None:
	default:
		break; // Draw nothing
	}

	// Recursively render children
	for (SceneNode* child : node->children)
		DrawNode(child, model);
}


/***********************************************************
 *  RenderScene()
 *
 *  This method is used for rendering the 3D scene by 
 *  transforming and drawing the basic 3D shapes
 ***********************************************************/
void SceneManager::RenderScene()
{
	// start the recursive drawing of the scene graph
	glm::mat4 identity = glm::mat4(1.0f);
	DrawNode(m_sceneParent, identity);
	if (m_sceneParent == nullptr) {
		std::cout << "m_sceneParent is NULL!" << std::endl;
	}
}

