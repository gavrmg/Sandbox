// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "Engine.h"
#include "GameFramework/Actor.h"
#include "ProceduralMeshComponent.h"
#include "TerrainObjectInterface.h"
//#include "Windows.h"
#include "ChunkMesh.generated.h"

const int BaseResolution = 64;
const float BaseVoxelSize = 200;
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
	
	//class TArray<ITerrainObjectInterface*> Objects;
protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;
//	TArray<ATestSphere*> Objects;
	int resolution = BaseResolution;
	float VoxelSize = BaseVoxelSize;
	bool IsRebuilding = false;
//	bool 
public:	
	// Called every frame
	virtual void Tick(float DeltaTime) override;
	void ConstructMesh();
	void UpdateObjects();
/*	const FBox& GetChunkBounds() {
		return ChunkBounds;
	}*/
private:
	struct GridData {
		float Value;
		ITerrainObjectInterface* Object;
		UMaterialInterface* Material;
	};

	struct Vertex {//Local representation of the mesh vertex
		FVector Position;
		FVector Normal;
		FLinearColor Color;
		FVector2D UV;
	};
	struct EdgeData {
		FVector Position;
		FVector Normal;
		Vertex NearestVertecies[4];
		UMaterialInterface* Material;
	};


	inline float lerpEdge(float a, float b) {
		if (abs(a - b) > 0.00001f)
			return (0 - a) / (b - a);
		else
			return 0;
	}

	struct Field {
		GridData**** Grid;
	};
	
	class MeshSectionUpdateThread : FRunnable {
		// Inherited via FRunnable
	private: 
		FProcMeshSection* UpdatedSection;
	public:
		virtual uint32 Run() override;
		int Material;
	};
};



