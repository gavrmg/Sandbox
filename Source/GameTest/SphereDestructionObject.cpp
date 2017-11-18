// Fill out your copyright notice in the Description page of Project Settings.

#include "SphereDestructionObject.h"


// Sets default values


// Sets default values
ASphereDestructionObject::ASphereDestructionObject()
{
	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	//Initialize fields. Create a SceneComponent instance. Not new as the instance should be registered in GC;
	//Use CreateDefaultSubibject<TYPE>();
	//TEXT Macro is used instead of String
	PrimaryActorTick.bCanEverTick = true;
	SceneComponent = CreateDefaultSubobject<USceneComponent>(TEXT("SceneComponent"));
	RootComponent = SceneComponent;
}

// Called when the game starts or when spawned
void ASphereDestructionObject::BeginPlay()
{
	Super::BeginPlay();
	Radius = 500;
	BoundingBox = FBox(this->GetTransform().GetTranslation() - Radius, this->GetTransform().GetTranslation() + Radius);
}

// Called every frame
void ASphereDestructionObject::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);

}
//Implementation of ITerrainObjectInterface funcs;
//Get the Signed distance function value at a point. The Point is a point in the global coord system, the object pos is fetched by using this->GetTransform().GetTranslation()
float ASphereDestructionObject::CalculateSignedDistanceFunction(const FVector& Point) {
	float dist = FVector::Dist(Point, this->GetTransform().GetTranslation());
	float value = dist - Radius;
	return value;
}
//Calculate the gradient at point
FVector ASphereDestructionObject::CalculateGradientAtPoint(const FVector& Point) {
	//FVector pos = this->GetTransform().GetTranslation();
	//FVector result = (Point - this->GetTransform().GetTranslation());
	FVector result = (this->GetTransform().GetTranslation()-Point);
	result.Normalize();

	return result;
}
//Return BoundingBox of and object
FBox ASphereDestructionObject::GetBoundingBox() {
	return BoundingBox;
}

