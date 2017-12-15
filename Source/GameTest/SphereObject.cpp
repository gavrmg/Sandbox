// Fill out your copyright notice in the Description page of Project Settings.

#include "SphereObject.h"


// Sets default values
ASphereObject::ASphereObject()
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
void ASphereObject::BeginPlay()
{
	Super::BeginPlay();
	Radius = 640;
	BoundingBox = FBox(this->GetTransform().GetTranslation() - Radius, this->GetTransform().GetTranslation() + Radius);
}

// Called every frame
void ASphereObject::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);

}
//Implementation of ITerrainObjectInterface funcs;
//Get the Signed distance function value at a point. The Point is a point in the global coord system, the object pos is fetched by using this->GetTransform().GetTranslation()
float ASphereObject::CalculateSignedDistanceFunction(const FVector& Point) {
	return FVector::Dist(Point, this->GetTransform().GetTranslation())-Radius;
}
//Calculate the gradient at point
FVector ASphereObject::CalculateGradientAtPoint(const FVector& Point) {
	//FVector pos = this->GetTransform().GetTranslation();
	FVector result = Point - this->GetTransform().GetTranslation();
	result.Normalize();
	
	return result;
}
//Return BoundingBox of and object
FBox ASphereObject::GetBoundingBox() {
	return BoundingBox;
}

