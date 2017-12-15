// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include <forward_list>
#include "CoreMinimal.h"
#include "GameFramework/Actor.h"
#include "TerrainObjectInterface.h"
#include "WorldObjectsHandler.generated.h"

const float ChunkSize = 1280;
const float Size = ChunkSize*4;//HalfSize of the level cube;

typedef struct Octree
{

	//Children;
	Octree* Nodes[8];
	//Pointer to parent
	Octree* Parent;

	//Array of objects in this octree;
	TArray<ITerrainObjectInterface*> Objects;
	//Queue of objects to be inserted
	
	FBox BoundingBox;
	float SideSize;//Size of the side; Captain Obvious for the resque!
	bool TreeBuilt = false;
	bool TreeReady = false;

	void Build(const FBox& Volume);
	void Delete();
	//Method stub
	TArray<ITerrainObjectInterface*> Search(const FBox& Borders);
};

UCLASS()
class GAMETEST_API AWorldObjectsHandler : public AActor//One actor per level
{
	GENERATED_BODY()
	
public:	
	// Sets default values for this actor's properties
	AWorldObjectsHandler();
	FBox Volume;
protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;

public:	
	// Called every frame
	virtual void Tick(float DeltaTime) override;
	void Init(const FVector& Position);
	Octree ObjectsContainer;

	


	
};

