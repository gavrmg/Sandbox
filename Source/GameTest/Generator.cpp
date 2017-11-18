// Fill out your copyright notice in the Description page of Project Settings.

#include "Generator.h"
#include "HeightMapObject.h"

// Sets default values
AGenerator::AGenerator()
{
 	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;

}

// Called when the game starts or when spawned
void AGenerator::BeginPlay()
{
	Super::BeginPlay();
	NoiseGen.SetFrequency(0.001);
}

// Called every frame
void AGenerator::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);

}

 void AGenerator::GenerateHeightMap(const FVector& Position)
{
	AHeightMapObject* map = GetWorld()->SpawnActor<AHeightMapObject>(Position,FRotator::ZeroRotator);
	int ChunkResolution = BaseResolution;
	float VoxelSize = BaseVoxelSize;
	int HeightMapResolution = resolution;

	FVector NormalizedPos = (Position / (VoxelSize*ChunkResolution)) * HeightMapResolution;
	FVector2D NormalizedPosPlanar;
	for(int i = 0; i <= HeightMapResolution; i++)
		for (int j = 0; j <= HeightMapResolution; j++) {
			NormalizedPosPlanar.X = (float)i + NormalizedPos.X;
			NormalizedPosPlanar.Y = (float)j + NormalizedPos.Y;
			
			float value = NoiseGen.GetValue(NormalizedPosPlanar.X, NormalizedPosPlanar.Y)
			+ 0.5*NoiseGen.GetValue(2 * NormalizedPosPlanar.X, 2 * NormalizedPosPlanar.Y)
			+ 0.25*NoiseGen.GetValue(4 * NormalizedPosPlanar.X, 4 * NormalizedPosPlanar.Y);
			value = (value + 1.) / 2;
			if (value > 1)
				map->GetHeightMap()[i][j] = 1. / value;
			else
				map->GetHeightMap()[i][j] = value;
		}
}

void AGenerator::SetGeneratorSeed(
	int seed
)
{
	this->seed = seed;
	NoiseGen.SetSeed(seed);
}

/*void AGenerator::GeneratieRegion(const ARegion& Region) {
	UWorld* world = GetWorld();
//	world->SpawnObject<AHeightMapObject>
}*/

