// Fill out your copyright notice in the Description page of Project Settings.

#include "Region.h"
#include "WorldObjectsHandler.h"
#include "ChunkMesh.h"
#include "SphereDestructionObject.h"
#include "PlayerCharacter.h"

// Sets default values
ARegion::ARegion()
{
 	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;
	SceneAnchor = CreateDefaultSubobject<USceneComponent>(TEXT("SceneAnchor"));
	RootComponent = SceneAnchor;
}

// Called when the game starts or when spawned
void ARegion::BeginPlay()
{
	Super::BeginPlay();
	RegionVolume = FBox(FVector(GetTransform().GetTranslation() - Size), FVector(GetTransform().GetTranslation() + Size));
//	APlayerCharacter::DigEvent::AddRaw(this, &ARegion::DigEvent_Implementation);
/*	for (AActor* obj : GetLevel()->Actors) {
		if (obj != nullptr) {
			if ((!obj->HasActorBegunPlay())&&(obj!=this))
				obj->DispatchBeginPlay();
			}
		}*/
	InitRegion();
}


// Called every frame
void ARegion::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);

}

void ARegion::InitRegion() {
	UWorld* world = GetWorld();
	handler = world->SpawnActor<AWorldObjectsHandler>();
	//handler->Volume = RegionVolume;
	if (!(handler->HasActorBegunPlay()))
		handler->DispatchBeginPlay();
	handler->Init(GetTransform().GetTranslation());
	/*for(int i = 0; i < 8; i++)
		for(int j = 0; j < 8; j++)
			for (int k = 0; k < 8; k++) {
				FVector location = ((FVector(i, j, k) - 4)*ChunkSize) + (ChunkSize / 2);
				Chunks.Add((AChunkMesh*)world->SpawnActor<AChunkMesh>(location, FRotator::ZeroRotator));
			}*/
	/*
	FVector location(-6400, -6400, 6400);
	Chunks.Add((AChunkMesh*)world->SpawnActor<AChunkMesh>(location, FRotator::ZeroRotator));
	*/
/*	int i, j, k;
	i = -4; j = -3; k = -4;
	FVector location = ((FVector(i, j, k)*ChunkSize) + (ChunkSize / 2));
	location.X = roundf(location.X);
	location.Y = roundf(location.Y);
	location.Z = roundf(location.Z);
	FActorSpawnParameters params;
	params.bAllowDuringConstructionScript = false;
	params.bNoFail = false;
	params.SpawnCollisionHandlingOverride = ESpawnActorCollisionHandlingMethod::AlwaysSpawn;
	AChunkMesh* mesh = (AChunkMesh*)world->SpawnActor<AChunkMesh>(location, FRotator::ZeroRotator, params);
	Chunks.Add(mesh);
*/
	for(int i = -4; i < 4; i++)
//	int i = 0;
		for(int j = -4; j < 4; j++)
			for (int k = -4; k < 4; k++) {
				FVector location = ((FVector(i, j, k)*ChunkSize) + (ChunkSize / 2));
				location.X = roundf(location.X);
				location.Y = roundf(location.Y);
				location.Z = roundf(location.Z);
				FActorSpawnParameters params;
				params.bAllowDuringConstructionScript = false;
				params.bNoFail = true;
				params.SpawnCollisionHandlingOverride = ESpawnActorCollisionHandlingMethod::AlwaysSpawn;
				AChunkMesh* mesh = (AChunkMesh*)world->SpawnActor<AChunkMesh>(location, FRotator::ZeroRotator,params);
				Chunks.Add(mesh);
			}
	int k = 0;
}

void ARegion::DigEvent_Implementation(FVector Position) {
	GEngine->AddOnScreenDebugMessage(-1, 5.f, FColor::Red, TEXT("MyEventRecieved!"));
	UWorld* world = GetWorld();
	FActorSpawnParameters params;
	params.bAllowDuringConstructionScript = false;
	params.bNoFail = true;
	params.SpawnCollisionHandlingOverride = ESpawnActorCollisionHandlingMethod::AlwaysSpawn;
	ITerrainObjectInterface* Obj = world->SpawnActor<ASphereDestructionObject>(Position, FRotator::ZeroRotator,params);
	Obj->SetTimeStamp(FDateTime::Now());
	if (Obj == nullptr) {
		int k = 0;
	}
//	((AActor*) Obj)->DispatchBeginPlay();
	handler->Init(GetTransform().GetTranslation());
//	handler->ObjectsContainer.Build(RegionVolume);
	for (AChunkMesh* chunk : Chunks) {
		if (Obj->GetBoundingBox().Intersect(chunk->ChunkBounds)) {
			chunk->UpdateObjects();
			chunk->ConstructMesh();
		}

	}
}

