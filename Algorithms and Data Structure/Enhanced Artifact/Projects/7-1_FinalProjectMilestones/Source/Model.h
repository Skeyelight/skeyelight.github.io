#pragma once
#include <vector>
#include <string>
#include <glm/glm.hpp>

// Simple mesh structure
struct Mesh {
    unsigned int vao;
    unsigned int vbo;
    unsigned int ebo;
    int indexCount;
};

// Simple model class
class Model {
public:
    Model() = default;
    bool LoadFromFile(const std::string& path);
    void Draw();

private:
    std::vector<Mesh> meshes;
};

