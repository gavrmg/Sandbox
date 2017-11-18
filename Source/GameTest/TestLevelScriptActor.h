// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "Engine/LevelScriptActor.h"
#include "TestLevelScriptActor.generated.h"


UCLASS()
class GAMETEST_API ATestLevelScriptActor : public ALevelScriptActor
{
	GENERATED_BODY()
	class AGenerator* Generator;

	UFUNCTION(BlueprintCallable, Category = LevelBlueprint)
		void SetupLevel();
};
