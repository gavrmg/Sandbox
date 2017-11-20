// Fill out your copyright notice in the Description page of Project Settings.

#include "WorldObjectsHandler.h"

std::forward_list<ITerrainObjectInterface*> InsertionObjectsList;

// Sets default values
AWorldObjectsHandler::AWorldObjectsHandler()
{
 	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;
	ObjectsContainer = Octree();

}

// Called when the game starts or when spawned
void AWorldObjectsHandler::BeginPlay()
{
	Super::BeginPlay();
}

// Called every frame
void AWorldObjectsHandler::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);

}

void AWorldObjectsHandler::Init(const FVector & Position)
{
	Volume = FBox(Position - Size, Position + Size);
	TArray<ITerrainObjectInterface*>* objs = new TArray<ITerrainObjectInterface*>();
	for (AActor* obj : GetLevel()->Actors) {
		if(obj!=nullptr){
			if (obj->Implements<UTerrainObjectInterface>()) {//Perform interface check 
				if (!obj->HasActorBegunPlay())
					obj->DispatchBeginPlay();
				ITerrainObjectInterface* current = Cast<ITerrainObjectInterface, AActor>(obj);
				if(current->GetBoundingBox().Intersect(this->Volume))
					objs->Add(current);
			}
		}
	}
	objs->Sort([]( ITerrainObjectInterface& A,  ITerrainObjectInterface& B)->bool{return (A.GetTimeStamp() > B.GetTimeStamp()); });
	for (ITerrainObjectInterface* obj : *objs) {
		InsertionObjectsList.push_front(obj);
	}
	delete objs;
	ObjectsContainer.Build(FBox(Position - FVector(Size, Size, Size), Position + FVector(Size,Size,Size)));
	InsertionObjectsList.clear();

}

void Octree::Build(const FBox& Volume) {
	this->BoundingBox = Volume;
	this->SideSize = Volume.GetSize().X;//Asserted that Volume is always a cube;
	if (this->SideSize <= ChunkSize) {
		for (ITerrainObjectInterface* obj : InsertionObjectsList) {
			if (BoundingBox.Intersect(obj->GetBoundingBox()))
				Objects.Add(obj);
		}
		return;
	}
	FBox Octants[8];
	float halfSize = Volume.GetExtent().X;
	//Create octants for children nodes;
	FVector Min, Max;
	Octants[0] = FBox(BoundingBox.Min, BoundingBox.GetCenter());
	Min = FVector(BoundingBox.Min.X, BoundingBox.GetCenter().Y, BoundingBox.Min.Z);
	Max = FVector(BoundingBox.GetCenter().X, BoundingBox.Max.Y, BoundingBox.GetCenter().Z);
	Octants[1] = FBox(Min, Max);

	Min = FVector(BoundingBox.GetCenter().X, BoundingBox.GetCenter().Y, BoundingBox.Min.Z);
	Max = FVector(BoundingBox.Max.X, BoundingBox.Max.Y, BoundingBox.GetCenter().Z);
	Octants[2] = FBox(Min, Max);

	Min = FVector(BoundingBox.GetCenter().X, BoundingBox.Min.Y, BoundingBox.Min.Z);
	Max = FVector(BoundingBox.Max.X, BoundingBox.GetCenter().Y, BoundingBox.GetCenter().Z);
	Octants[3] = FBox(Min, Max);

	Min = FVector(BoundingBox.Min.X, BoundingBox.Min.Y, BoundingBox.GetCenter().Z);
	Max = FVector(BoundingBox.GetCenter().X, BoundingBox.GetCenter().Y, BoundingBox.Max.Z);
	Octants[4] = FBox(Min, Max);

	Min = FVector(BoundingBox.Min.X, BoundingBox.GetCenter().Y, BoundingBox.GetCenter().Z);
	Max = FVector(BoundingBox.GetCenter().X, BoundingBox.Max.Y, BoundingBox.Max.Z);
	Octants[5] = FBox(Min, Max);

	Min = FVector(BoundingBox.GetCenter().X, BoundingBox.GetCenter().Y, BoundingBox.GetCenter().Z);
	Max = FVector(BoundingBox.Max.X, BoundingBox.Max.Y, BoundingBox.Max.Z);
	Octants[6] = FBox(Min, Max);

	Min = FVector(BoundingBox.GetCenter().X, BoundingBox.Min.Y, BoundingBox.GetCenter().Z);
	Max = FVector(BoundingBox.Max.X, BoundingBox.GetCenter().Y, BoundingBox.Max.Z);
	Octants[7] = FBox(Min, Max);


	for (int i = 0; i < 8; i++) {
		Nodes[i] = new Octree();
		Nodes[i]->Parent = this;
		Nodes[i]->Build(Octants[i]);
	}
	return;
}

void Octree::Delete() {
	if (!((Nodes[0]->SideSize) < ChunkSize)){
		for (int i = 0; i < 8; i++) {
			Nodes[i]->Delete();
			delete Nodes[i];
		}
	}
}

TArray<ITerrainObjectInterface*> Octree::Search(const FBox& Borders) {
	TArray<ITerrainObjectInterface*> result;
	Octree* Node = this;
	Octree* NextNode = this;
	while (!(NextNode->BoundingBox==Borders)) {
		Node = NextNode;
		for (int i = 0; i < 8; i++)
			if (Node->Nodes[i]->BoundingBox.IsInsideOrOn(Borders.GetCenter())) {
				NextNode = Node->Nodes[i];
				break;
			}
	}
	return TArray<ITerrainObjectInterface*>(NextNode->Objects);
}