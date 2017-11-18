// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "GameFramework/Actor.h"
#include "ChunkMesh.h"
#include "Region.generated.h"

UCLASS()
class GAMETEST_API ARegion : public AActor
{
	GENERATED_BODY()
	
public:	
	// Sets default values for this actor's properties
	ARegion();
	UPROPERTY()
		USceneComponent* SceneAnchor;
	UPROPERTY()
		TArray<AChunkMesh*> Chunks;
	UPROPERTY()
		class AWorldObjectsHandler* handler;
	UPROPERTY()
		FBox RegionVolume;
protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;
public:	
	// Called every frame
	virtual void Tick(float DeltaTime) override;
	void InitRegion();
	void DigEvent_Implementation(FVector Position);
	
};
