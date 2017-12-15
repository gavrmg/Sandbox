// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include <cmath>
#include <utility>
#include <cstdio>


#include "CoreMinimal.h"
#include "Engine.h"
#include "GameFramework/Actor.h"
#include "ProceduralMeshComponent.h"
#include "TerrainObjectInterface.h"
#include "GenericOctree.h"
//#include "Windows.h"
#include "Allocator/Allocator.h"
#include "ChunkMesh.generated.h"

const int BaseResolution = 128;
const float BaseVoxelSize = 10;

typedef std::pair<float, ITerrainObjectInterface*> VoxelVertexData;

struct GridData {
	float Value;
	ITerrainObjectInterface* Object;
	UMaterialInterface* Material;
	const size_t size = 0;
	
};

struct Vertex {//Local representation of the mesh vertex
	FVector Position;
	FVector Normal;
	FLinearColor Color;
	FVector2D UV;
	const size_t size = sizeof(FVector) * 3 + sizeof(FLinearColor) + sizeof(FVector2D);
};
struct EdgeData {
	FVector Position;
	FVector Normal;
	Vertex NearestVertecies[4];
	UMaterialInterface* Material;
	//TOctree<
};

enum OctreeNodeType {
	Node_None,//Not a node
	Node_Internal,//An internal nod of the tree
	Node_Inside,//Node fully inside or outside the objects
	Node_Pseudo,
	Node_Leaf//Leaf node
};
class OctreeDrawInfo {
	DECLARE_ALLOCATOR
public:
	float QEFpos[36]{ -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1 };
	float QEFnormals[36]{ -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1 };
	FVector AverageNormal;
	FVector Position;
	int Materials[8];//corresponds to the material index in the chunk's MaterialList, -1 represents air;
	int32 index = -1;
	int corners;
	OctreeDrawInfo() {
		corners = 0;
	}
};

class vOctree;

UCLASS()
class GAMETEST_API AChunkMesh : public AActor
{
	GENERATED_BODY()
	
public:	
	// Sets default values for this actor's properties
	AChunkMesh();

	UPROPERTY(EditAnywhere, BlueprintReadWrite)
		class UProceduralMeshComponent* Mesh;
	TArray<ITerrainObjectInterface*> Objects;
	UPROPERTY()
		FBox ChunkBounds;
	TArray<UMaterial*> MaterialList;
	class vOctree* VoxelOctree;
	
	//class TArray<ITerrainObjectInterface*> Objects;
protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;
//	TArray<ATestSphere*> Objects;
	int resolution = BaseResolution;
	float UnitPerVoxel = BaseVoxelSize;
	bool IsRebuilding = false;
	bool MeshDataBuilding = false;
	//TArray<FMeshCalculationThread> ChildThreads;
//	bool 
public:	
	// Called every frame
	virtual void Tick(float DeltaTime) override;
	//void DataReady_Implementation(AChunkMesh* Chunk, FMeshCalculationThread* Thread);
	void ConstructMesh();
	void UpdateObjects();


	const FBox& GetChunkBounds() {
		return ChunkBounds;
	}
	static inline float lerpEdge(float a, float b) {
		if (abs(a - b) > 0.00001f)
			return (0 - a) / (b - a);
		else {
			if (a - b > 0)
				return 0;
			else
				return 1;
		}
	}

	VoxelVertexData CalcCSGAtPoint(const FVector& Point);


	
};




class ChunkOctreeNode {
	DECLARE_ALLOCATOR
public:
	const float minsize = 20;
	ChunkOctreeNode(FBox boundingBox, AChunkMesh* chunk) {
		this->BoundingBox = boundingBox;
		//this->type = type;
		this->Chunk = chunk;
		this->DrawInfo = nullptr;
	};



	~ChunkOctreeNode() {
		if (type != Node_Leaf) {
			for (int i = 0; i < 8; i++) {
				if (Children[i] != nullptr) {
					delete Children[i];
					Children[i] = nullptr;
				}
			}
			//delete[] Children;
			Chunk = nullptr;
		}
		else {
			if (DrawInfo != nullptr)
				delete DrawInfo;
		}
	}
	AChunkMesh* Chunk;
	ChunkOctreeNode* Parent;
	void ConstructOctreeNodes();
	OctreeNodeType type;
	FBox BoundingBox;
	float size;
	ChunkOctreeNode* Children[8];
	OctreeDrawInfo* DrawInfo = nullptr;

};

class vOctree {
public:
	vOctree() {};
	~vOctree() {};
	ChunkOctreeNode* root;
	
};

