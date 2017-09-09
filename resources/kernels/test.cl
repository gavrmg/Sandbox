__kernel void test(__global int* message)
{
	// получаем текущий id.
	int gid = get_global_id(0);
	//printf("%f",gid);
//	int gid = 0;
	message[gid] += gid;
}