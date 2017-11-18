// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "GameFramework/Actor.h"
#include "TerrainObjectInterface.h"
#include "HeightMapObject.generated.h"
const int resolution = 1024;
UCLASS()
class GAMETEST_API AHeightMapObject : public AActor, public ITerrainObjectInterface
{
	GENERATED_BODY()
	
public:	
	// Sets default values for this actor's properties
	AHeightMapObject();
	UPROPERTY(EditAnywhere)
		USceneComponent* SceneComponent;
protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;
	//One map for 100x100 meteres;
	int HeightMapSize;
	float** HeightMap;
	FVector LocalPoint;
	FBox BoundingBox;
public:	
	// Called every frame
	virtual void Tick(float DeltaTime) override;
	virtual float CalculateSignedDistanceFunction(const FVector& Point) override;
	virtual FVector CalculateGradientAtPoint(const FVector& Point) override;
	virtual FBox GetBoundingBox() override;
	float** GetHeightMap();
	void SetHeightMap(float** HeightMap);
	virtual CSG_TYPE_ENUM GetCSGType() override {
		return CSG_TYPE_ENUM::CSG_OR;
	};
	virtual void EndPlay(const EEndPlayReason::Type EndPlayReason) override;

	
};
