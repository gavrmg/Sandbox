// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "GameFramework/Actor.h"
#include "TerrainObjectInterface.h"
#include "SphereObject.generated.h"

UCLASS()
class GAMETEST_API ASphereObject : public AActor, public ITerrainObjectInterface
{
	GENERATED_BODY()
	
public:	
	// Sets default values for this actor's properties
	ASphereObject();
	//Macros needed so that object's transform would be visible in the IDE;
	UPROPERTY(EditAnywhere)
		USceneComponent* SceneComponent;//Scene component containing object's transform
	float Radius;
protected:
	// Called when the game starts or when spawned
	FVector LocalPoint;
	FBox BoundingBox;
	virtual void BeginPlay() override;
public:	
	// Called every frame
	virtual void Tick(float DeltaTime) override;
	virtual float CalculateSignedDistanceFunction(const FVector& Point) override;
	virtual FVector CalculateGradientAtPoint(const FVector& Point) override;
	virtual FBox GetBoundingBox() override;
	virtual CSG_TYPE_ENUM GetCSGType() override {
		return CSG_TYPE_ENUM::CSG_OR;
	};

	
};
