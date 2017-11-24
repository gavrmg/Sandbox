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
struct MeshData {
	MeshData(TArray<FVector>* vertecies, TArray<int32>* indecies, TArray<FVector>* normals, TArray<FVector2D>* uvs, TArray<FLinearColor>* colors, TArray<FProcMeshTangent>* tangents) {
		Vertecies = vertecies;
		Indecies = indecies;
		Normals = normals;
		UVs = uvs;
		Colors = colors;
		Tangents = tangents;
	}
	TArray<FVector>* Vertecies;
	TArray<int32>* Indecies;
	TArray<FVector>* Normals;
	TArray<FVector2D>* UVs;
	TArray<FLinearColor>* Colors;
	TArray<FProcMeshTangent>* Tangents;

};
struct ChunkData {
	int ChunkResolution;
	GridData**** grid;
	EdgeData***** edges;
	Vertex**** voxels;
	ChunkData() {
		ChunkResolution = 64;
	}

};

class FMeshCalculationMultithread {

public:
	FMeshCalculationMultithread();
	~FMeshCalculationMultithread();
	void BuildMesh(bool IsInitiating);
	class AChunkMesh* Chunk;
	ChunkData* ThisChunkData;
	TArray<MeshData*> OutputArray;
	FGraphEventArray GridBuilding_CompleteEvent;
	FGraphEventArray MeshSectionCalculation_CompleteEvents;
	bool GetIsGridComplete();
	bool AreCalculationsComplete();
	//TODO: Add GridBuildingTask
	void BuildGrid();
	//Actual task code, mesh building algo goes here;
	MeshData* CalculateMeshSection(int32 SectionNumber);
	FORCEINLINE void EmptyOutput() {
		for (int i = 0; i < OutputArray.Num(); i++) {
			delete OutputArray[i]->Vertecies;
			delete OutputArray[i]->Indecies;
			delete OutputArray[i]->Normals;
			delete OutputArray[i]->UVs;
			delete OutputArray[i]->Colors;
			delete OutputArray[i]->Tangents;
			delete OutputArray[i];
		}
	}
	void ResetGridData();
	class FGridBuildingTask {
	public:

		FGridBuildingTask(FMeshCalculationMultithread* ThreadDataContainer);

		~FGridBuildingTask() {};

		FMeshCalculationMultithread* base;
		static const TCHAR* GetTaskName() {
			return TEXT("GridBuildingTask");
		}

		FORCEINLINE TStatId GetStatId() const { RETURN_QUICK_DECLARE_CYCLE_STAT(FGridBuildingTask, STATGROUP_TaskGraphTasks); }

		static ENamedThreads::Type GetDesiredThread() {
			return ENamedThreads::AnyThread;
		}
		static ESubsequentsMode::Type GetSubsequentsMode()
		{
			return ESubsequentsMode::TrackSubsequents;
		}


		void DoTask(ENamedThreads::Type CurrentThread, const FGraphEventRef& CompletionGraph);
	};
	class FMeshDataBuildingTask {
	public:
		FMeshDataBuildingTask(int32 SectionNumber, FMeshCalculationMultithread* ThreadDataContainer);

		~FMeshDataBuildingTask() {};
		FMeshCalculationMultithread* base;
		int32 sectionNumber;
		static const TCHAR* GetTaskName() {
			return TEXT("MeshDataBuildingTask");
		}

		FORCEINLINE TStatId GetStatId() const { RETURN_QUICK_DECLARE_CYCLE_STAT(FMeshDataBuildingTask, STATGROUP_TaskGraphTasks); }

		static ENamedThreads::Type GetDesiredThread() {
			return ENamedThreads::AnyThread;
		}

		static ESubsequentsMode::Type GetSubsequentsMode()
		{
			return ESubsequentsMode::TrackSubsequents;
		}


		void DoTask(ENamedThreads::Type CurrentThread, const FGraphEventRef& CompletionGraph);
	};


	inline float lerpEdge(float a, float b) {
		if (abs(a - b) > 0.00001f)
			return (0 - a) / (b - a);
		else
			return 0;
	}

};


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
	bool MeshDataBuilding = false;
	class FMeshCalculationMultithread* Thread = new FMeshCalculationMultithread();
	//TArray<FMeshCalculationThread> ChildThreads;
//	bool 
public:	
	// Called every frame
	virtual void Tick(float DeltaTime) override;
	//void DataReady_Implementation(AChunkMesh* Chunk, FMeshCalculationThread* Thread);
	void ConstructMesh();
	void UpdateObjects();
/*	void StartRebuilding() {
		NeedsRebuilding = true;
	}*/
	void SetRebuilding(bool State) {
		IsRebuilding = State;
	}
	bool GetIsRebuilding() {
		return IsRebuilding;
	}
	const FBox& GetChunkBounds() {
		return ChunkBounds;
	}

	
};



