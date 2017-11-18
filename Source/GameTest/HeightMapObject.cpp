// Fill out your copyright notice in the Description page of Project Settings.

#include "HeightMapObject.h"
#include "WorldObjectsHandler.h"


// Sets default values
AHeightMapObject::AHeightMapObject()
{
 	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;
	SceneComponent = CreateDefaultSubobject<USceneComponent>(TEXT("SceneComponent"));
	RootComponent = SceneComponent;
}

// Called when the game starts or when spawned
void AHeightMapObject::BeginPlay()
{
	Super::BeginPlay();
	HeightMapSize = powf(resolution + 1, 2);
	HeightMap = new float*[resolution+1];
	for (int i = 0; i <= resolution; i++)
		HeightMap[i] = new float[resolution + 1];
	FVector pos = GetTransform().GetTranslation();
	FVector Min,Max;
	Min.X = pos.X;
	Min.Y = pos.Y;
	Min.Z = 0;
	Max.X = pos.X + ChunkSize;
	Max.Y = pos.Y + ChunkSize;
	Max.Z = 12800;
	/*for (int i = 0; i < resolution+1; i++)
		for (int j = 0; j < resolution+1; j++)
			HeightMap[(int)(i*(resolution + 1)) + j] = 0;*/

	BoundingBox = FBox(Min, Max);
}
// Called every frame
void AHeightMapObject::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);

}
int Pos1D;
float AHeightMapObject::CalculateSignedDistanceFunction(const FVector& Point) {
	//LocalPoint = Point - GetTransform().GetTranslation();
	LocalPoint.X = Point.X - GetTransform().GetTranslation().X;
	LocalPoint.Y = Point.Y - GetTransform().GetTranslation().Y;
	LocalPoint.Z = Point.Z;
	//LocalPoint /= ChunkSize;
	LocalPoint.X =  (LocalPoint.X/ChunkSize)*(resolution+1);
	LocalPoint.Y =  (LocalPoint.Y / ChunkSize)*(resolution + 1);
	LocalPoint.Z =  LocalPoint.Z/(ChunkSize);
	if (LocalPoint.X >= 0 && LocalPoint.X <= resolution && LocalPoint.Y >= 0 && LocalPoint.Y <= resolution)
	{
		float value = LocalPoint.Z - HeightMap[(int)LocalPoint.X][(int)LocalPoint.Y];
		if (LocalPoint.Z > 1.0f && value < 0.0f)
			int k = 0;
		return LocalPoint.Z - HeightMap[(int)LocalPoint.X][(int)LocalPoint.Y];
	}
	else
		return 1;
}

FVector AHeightMapObject::CalculateGradientAtPoint(const FVector& Point) {
	LocalPoint = Point - GetTransform().GetTranslation();
	LocalPoint /= ChunkSize;
	LocalPoint *= (resolution + 1);
	FVector result;
	result.Z = 1;
	if (LocalPoint.X >= 0 && LocalPoint.X < resolution && LocalPoint.Y >= 0 && LocalPoint.Y < resolution) {
		result.X = HeightMap[(int)LocalPoint.X + 1][(int)LocalPoint.Y] - HeightMap[(int)LocalPoint.X][(int)LocalPoint.Y];
		result.Y = HeightMap[(int)LocalPoint.X][(int)LocalPoint.Y + 1] - HeightMap[(int)LocalPoint.X][(int)LocalPoint.Y];
		result.Normalize();
	}
	else
		result = FVector::UpVector;
	//result;
	return result;
}

FBox AHeightMapObject::GetBoundingBox() {
	return BoundingBox;
}

float** AHeightMapObject::GetHeightMap() {
	return HeightMap;
}

void AHeightMapObject::SetHeightMap(float** HeightMap) {
	this->HeightMap = HeightMap;
}

void AHeightMapObject::EndPlay(const EEndPlayReason::Type EndPlayReason)
{
	Super::EndPlay(EndPlayReason);
	for (int i = 0; i <= resolution; i++) {
		delete[] HeightMap[i];
	}
	delete[] HeightMap;
}
