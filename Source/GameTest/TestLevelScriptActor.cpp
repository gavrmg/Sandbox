// Fill out your copyright notice in the Description page of Project Settings.

#include "TestLevelScriptActor.h"
#include "Region.h"
#include "Generator.h"
#include "PlayerCharacter.h"

void ATestLevelScriptActor::SetupLevel()
{
	UWorld* world = GetWorld();
	Generator = world->SpawnActor<AGenerator>();
	/*for(int i = -4; i < 4; i++)
		for(int j = -4; j < 4; j++)
			Generator->GenerateHeightMap(FVector(i,j,0)*BaseResolution*BaseVoxelSize);*/
	ARegion * Region = world->SpawnActor<ARegion>(FVector::ZeroVector, FRotator::ZeroRotator);
	APlayerCharacter* PlayerCharacter = world->SpawnActor<APlayerCharacter>(FVector(640, 640, 1450), FRotator::ZeroRotator);
	PlayerCharacter->CharacterDigEvent.AddUObject(Region, &ARegion::DigEvent_Implementation);
	world->GetFirstPlayerController()->Possess(PlayerCharacter);
//	PlayerCharacter->
	//	PlayerCharacter->SetupPlayerInputComponent(world->GetFirstPlayerController);
}
