// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "GameFramework/Actor.h"
#include "FastNoise.h"
#include "Region.h"
#include "Generator.generated.h"

UCLASS()
class GAMETEST_API AGenerator : public AActor
{
	GENERATED_BODY()
	
public:	
	// Sets default values for this actor's properties
	AGenerator();

protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;
	long seed;

public:	
	// Called every frame
	virtual void Tick(float DeltaTime) override;
	UFUNCTION(BlueprintCallable,Category = Actor)
		void GenerateHeightMap(const FVector& Position);
	UFUNCTION(BlueprintCallable, Category = Actor)
		void SetGeneratorSeed(
			int seed
		);
	FastNoise NoiseGen;

/*	UFUNCTION(BlueprintCallable, Category = Actor)
		int GetGeneratorSeed() { return seed; };*/

	
};
