// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "GameFramework/Character.h"
#include <EngineGlobals.h>
#include <Runtime/Engine/Classes/Engine/Engine.h>
#include "PlayerCharacter.generated.h"

DECLARE_EVENT_OneParam(APlayerCharacter, DigEvent, FVector)
class UInputComponent;
UCLASS()
class GAMETEST_API APlayerCharacter : public ACharacter
{
	GENERATED_BODY()

public:
	// Sets default values for this character's properties
	APlayerCharacter();

protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;
	void MoveForward(float Value);
	void MoveRight(float Value);
	void TurnAtRate(float Rate);
	void LookUpAtRate(float Rate);
	void Dig();
public:	
	// Called every frame
	virtual void Tick(float DeltaTime) override;

	// Called to bind functionality to input
	virtual void SetupPlayerInputComponent(class UInputComponent* PlayerInputComponent) override;
	class UCameraComponent* Camera;
	UPROPERTY(EditAnywhere)
		class USceneComponent* VisibleComponent;
	float BaseTurnRate;
	float BaseLookUpRate;
	DigEvent CharacterDigEvent;
};
