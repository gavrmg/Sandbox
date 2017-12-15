// Fill out your copyright notice in the Description page of Project Settings.

#include "PlayerCharacter.h"
#include "Windows.h"
#include "Camera/CameraComponent.h"
#include "Components/CapsuleComponent.h"
#include "Components/InputComponent.h"
//#include "Components/CapsuleCollisionComponent.h"

// Sets default values
APlayerCharacter::APlayerCharacter()
{
	BaseTurnRate = 45.f;
	BaseLookUpRate = 45.f;

 	// Set this character to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;
	GetCapsuleComponent()->InitCapsuleSize(55.f, 96.0f);
	GetCapsuleComponent()->SetCollisionEnabled(ECollisionEnabled::QueryAndPhysics);
	GetCapsuleComponent()->SetSimulatePhysics(false);
	//Basic setup for camera and visible components
	//RootComponent = CreateDefaultSubobject<USceneComponent>(TEXT("RootComponent"));
	VisibleComponent = CreateDefaultSubobject<USceneComponent>(TEXT("VisibleComponentStub"));
	Camera = CreateDefaultSubobject<UCameraComponent>(TEXT("Camera"));
	Camera->SetupAttachment(GetCapsuleComponent());
	Camera->RelativeLocation = FVector(0, 0, 0.85f);
	Camera->bUsePawnControlRotation = true;
//	Camera->SetRelativeLocation(0, 0, 80);
	VisibleComponent->SetupAttachment(RootComponent);
}

// Called when the game starts or when spawned
void APlayerCharacter::BeginPlay()
{
	Super::BeginPlay();
	
}

void APlayerCharacter::MoveForward(float Value)
{
	if (Value != 0.0)
		AddMovementInput(GetActorForwardVector(), Value);
}

void APlayerCharacter::MoveRight(float Value)
{
	if (Value != 0.0)
		AddMovementInput(GetActorRightVector(), Value);

}

void APlayerCharacter::TurnAtRate(float Rate)
{
	AddControllerYawInput(Rate*BaseTurnRate*GetWorld()->GetDeltaSeconds());
}

void APlayerCharacter::LookUpAtRate(float Rate)
{
	AddControllerPitchInput(Rate*BaseLookUpRate*GetWorld()->GetDeltaSeconds());

}

void APlayerCharacter::Dig()
{
	SYSTEMTIME start, stop;
	GetSystemTime(&start);
	FVector CameraLoc;
	FRotator CameraRot;
	GetActorEyesViewPoint(CameraLoc, CameraRot);
	FVector Start = CameraLoc;
	FVector End = Start + CameraRot.Vector() * 400;
	FHitResult HitResult;
	FCollisionQueryParams QueryParams;
	FCollisionObjectQueryParams QueryObjectParams;
	QueryParams.bTraceComplex = true;
	QueryParams.bTraceAsyncScene = true;
	QueryParams.bReturnPhysicalMaterial = true;
	QueryParams.AddIgnoredActor(this);
	QueryObjectParams.RemoveObjectTypesToQuery(ECC_Pawn);
	GetSystemTime(&stop);

//	GEngine->AddOnScreenDebugMessage(-1, 5.f, FColor::Red, FString::FromInt(stop.wMilliseconds-start.wMilliseconds));
	if (GetWorld()->LineTraceSingleByObjectType(HitResult, Start, End, QueryObjectParams, QueryParams)) {
		CharacterDigEvent.Broadcast(HitResult.Location);
	}
}

// Called every frame
void APlayerCharacter::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);

}

// Called to bind functionality to input
void APlayerCharacter::SetupPlayerInputComponent(UInputComponent* PlayerInputComponent)
{
	Super::SetupPlayerInputComponent(PlayerInputComponent);
//	InputComponent->
	//check(PlayerInputComponent) WTF is this?

	PlayerInputComponent->BindAction("Jump", IE_Pressed, this, &ACharacter::Jump);
	PlayerInputComponent->BindAction("Jump", IE_Released, this, &ACharacter::StopJumping);
	PlayerInputComponent->BindAction("Action", IE_Pressed, this, &APlayerCharacter::Dig);
	PlayerInputComponent->BindAxis("MoveForward", this, &APlayerCharacter::MoveForward);
	PlayerInputComponent->BindAxis("MoveRight", this, &APlayerCharacter::MoveRight);
	PlayerInputComponent->BindAxis("Turn", this, &APawn::AddControllerYawInput);
	PlayerInputComponent->BindAxis("TurnAtRate", this, &APlayerCharacter::TurnAtRate);
	PlayerInputComponent->BindAxis("LookUp", this, &APawn::AddControllerPitchInput);
	PlayerInputComponent->BindAxis("LookUpAtRate", this, &APlayerCharacter::LookUpAtRate);

}

