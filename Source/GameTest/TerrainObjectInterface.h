// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "TerrainObjectInterface.generated.h"

// This class does not need to be modified.

UENUM()
enum class CSG_TYPE_ENUM : uint8 {
	CSG_OR, CSG_AND, CSG_DIFF
};

	
UINTERFACE(MinimalAPI)
class UTerrainObjectInterface : public UInterface
{
	GENERATED_BODY()
};

/**
 * 
 */
class GAMETEST_API ITerrainObjectInterface
{
	GENERATED_BODY()
	// Add interface functions to this class. This is the class that will be inherited to implement this interface.
public:
	virtual float CalculateSignedDistanceFunction(const FVector& Point) = 0;
	virtual FVector CalculateGradientAtPoint(const FVector& Point) = 0;
	virtual FBox GetBoundingBox() = 0;
	virtual CSG_TYPE_ENUM GetCSGType() = 0;
	FDateTime GetTimeStamp() { return TimeStamp; };
	void SetTimeStamp(FDateTime TimeStamp) {
		this->TimeStamp = TimeStamp;
	}
protected:
	FDateTime TimeStamp;
};
