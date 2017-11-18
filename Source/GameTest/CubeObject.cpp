// Fill out your copyright notice in the Description page of Project Settings.

#include "CubeObject.h"
#include <algorithm>


// Sets default values
ACubeObject::ACubeObject()
{
 	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;
	SceneComponent = CreateDefaultSubobject<USceneComponent>(TEXT("SceneComponent"));
	RootComponent = SceneComponent;	
	Extents = FVector(500, 500, 200);
}

// Called when the game starts or when spawned
void ACubeObject::BeginPlay()
{
	Super::BeginPlay();
	BoundingBox = FBox(this->GetTransform().GetTranslation() - Extents, this->GetTransform().GetTranslation() + Extents);
	
}

// Called every frame
void ACubeObject::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);

}

float ACubeObject::CalculateSignedDistanceFunction(const FVector & Point)
{
	FVector Distance = (Point - this->GetTransform().GetTranslation()).GetAbs();
	Distance -= Extents;
	/*float inner;
	if ((Distance.X <= 0 && Distance.Y <=0) && Distance.Z <= 0)
		inner = Distance.GetMax();
	else
		inner = 0;
	
	
	float outer;

	
	if ((Distance.X > 0 || Distance.Y > 0) || Distance.Z > 0)
		outer = Distance.GetMax();
	else outer = 0;
	float result;
	result = inner + outer;
	if (result == 0 || isnan(result))
		int k = 0;
	return inner + outer;*/
	return std::fminf(Distance.GetMax(), 0)+std::fmaxf(0,Distance.GetMax());
}
const float delta = 0.1f;
FVector ACubeObject::CalculateGradientAtPoint(const FVector & Point)
{
	FVector result;
	FVector temp;
	for (int i = 0; i < 3; i++) {
		temp = Point;
		temp.Component(i) += delta;
		result.Component(i) = CalculateSignedDistanceFunction(temp) - CalculateSignedDistanceFunction(Point);
	}
	result.Normalize();
	return result;
}

FBox ACubeObject::GetBoundingBox()
{
	return FBox(this->GetTransform().GetTranslation() - Extents, this->GetTransform().GetTranslation() + Extents);
}

