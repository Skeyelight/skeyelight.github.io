#define TINYGLTF_IMPLEMENTATION
#define STB_IMAGE_IMPLEMENTATION
#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "tiny_gltf.h"
#include "Model.h"
#include <GL/glew.h>

// Loads a glTF 2.0 model from a file and prepares it for rendering
bool Model::LoadFromFile(const std::string& path)
{
    tinygltf::Model gltfModel;
    tinygltf::TinyGLTF loader;

    std::string err, warn;

    bool ret = loader.LoadBinaryFromFile(&gltfModel, &err, &warn, path);
    if (!ret) return false;

	// Process each mesh in the glTF model
    for (const auto& mesh : gltfModel.meshes)
    {
        for (const auto& primitive : mesh.primitives)
        {
            if (!primitive.attributes.count("POSITION"))
                continue;

			// Load positions, normals, and texture coordinates
            int posIndex = primitive.attributes.at("POSITION");
            int normIndex = primitive.attributes.at("NORMAL");
            int uvIndex = primitive.attributes.at("TEXCOORD_0");

			// Accessors
            const tinygltf::Accessor& posAccessor = gltfModel.accessors[posIndex];
            const tinygltf::Accessor& normAccessor = gltfModel.accessors[normIndex];
            const tinygltf::Accessor& uvAccessor = gltfModel.accessors[uvIndex];

			// BufferViews
            const tinygltf::BufferView& posView = gltfModel.bufferViews[posAccessor.bufferView];
            const tinygltf::BufferView& normView = gltfModel.bufferViews[normAccessor.bufferView];
            const tinygltf::BufferView& uvView = gltfModel.bufferViews[uvAccessor.bufferView];

			// Buffers
            const tinygltf::Buffer& posBuffer = gltfModel.buffers[posView.buffer];
            const tinygltf::Buffer& normBuffer = gltfModel.buffers[normView.buffer];
            const tinygltf::Buffer& uvBuffer = gltfModel.buffers[uvView.buffer];

			// Extract vertex data
            const float* posData = reinterpret_cast<const float*>(
                &posBuffer.data[posView.byteOffset + posAccessor.byteOffset]);

            const float* normData = reinterpret_cast<const float*>(
                &normBuffer.data[normView.byteOffset + normAccessor.byteOffset]);

            const float* uvData = reinterpret_cast<const float*>(
                &uvBuffer.data[uvView.byteOffset + uvAccessor.byteOffset]);

            std::vector<float> vertexData;
            vertexData.reserve(posAccessor.count * 8);

            for (size_t i = 0; i < posAccessor.count; i++)
            {
                // Position
                vertexData.push_back(posData[i * 3 + 0]);
                vertexData.push_back(posData[i * 3 + 1]);
                vertexData.push_back(posData[i * 3 + 2]);

                // Normal
                vertexData.push_back(normData[i * 3 + 0]);
                vertexData.push_back(normData[i * 3 + 1]);
                vertexData.push_back(normData[i * 3 + 2]);

                // UV
                vertexData.push_back(uvData[i * 2 + 0]);
                vertexData.push_back(1.0f - uvData[i * 2 + 1]);
            }

            // Load indices
            const tinygltf::Accessor& indexAccessor = gltfModel.accessors[primitive.indices];
            const tinygltf::BufferView& indexView = gltfModel.bufferViews[indexAccessor.bufferView];
            const tinygltf::Buffer& indexBuffer = gltfModel.buffers[indexView.buffer];

            const unsigned short* indices =
                reinterpret_cast<const unsigned short*>(
                    &indexBuffer.data[indexView.byteOffset + indexAccessor.byteOffset]);
			
            // Create OpenGL buffers and upload data
            Mesh m;

            glGenVertexArrays(1, &m.vao);
            glGenBuffers(1, &m.vbo);
            glGenBuffers(1, &m.ebo);

            glBindVertexArray(m.vao);

            // Upload interleaved vertex data
            glBindBuffer(GL_ARRAY_BUFFER, m.vbo);
            glBufferData(GL_ARRAY_BUFFER,
                vertexData.size() * sizeof(float),
                vertexData.data(),
                GL_STATIC_DRAW);

            GLsizei stride = sizeof(float) * 8;

            // Position (location = 0)
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, stride, (void*)0);

            // Normal (location = 1)
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, stride,
                (void*)(sizeof(float) * 3));

            // UV (location = 2)
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, stride,
                (void*)(sizeof(float) * 6));

            // Upload indices
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m.ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER,
                indexAccessor.count * sizeof(unsigned short),
                indices,
                GL_STATIC_DRAW);

            m.indexCount = indexAccessor.count;

            glBindVertexArray(0);

            meshes.push_back(m);
        }
    }

    return true;
}

// Draws the model by rendering each mesh
void Model::Draw()
{
    for (auto& m : meshes)
    {
        glBindVertexArray(m.vao);
        glDrawElements(GL_TRIANGLES, m.indexCount, GL_UNSIGNED_SHORT, 0);
    }
    glBindVertexArray(0);
}
